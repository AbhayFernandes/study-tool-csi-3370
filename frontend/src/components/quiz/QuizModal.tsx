import React, { useEffect, useState } from 'react';
import { X, RefreshCw, FolderOpen, AlignLeft, CheckCircle, XCircle } from 'lucide-react';
import { Button } from '../ui/button';
import { Alert, AlertDescription } from '../ui/alert';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

export interface QuizQuestion {
  id: string;
  question: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  correctOption: number; // 1-4
}

export interface QuizDto {
  id: string;
  title: string;
  questions: QuizQuestion[];
  createdAt: string;
}

interface QuizModalProps {
  userId: string;
  isOpen: boolean;
  onClose: () => void;
}

const QuizModal: React.FC<QuizModalProps> = ({ userId, isOpen, onClose }) => {
  const [quiz, setQuiz] = useState<QuizDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [content, setContent] = useState('');
  const [files, setFiles] = useState<any[]>([]);
  const [fetchingFiles, setFetchingFiles] = useState(false);
  const [answers, setAnswers] = useState<Record<string, number>>({});
  const [submitted, setSubmitted] = useState(false);

  // Close on ESC
  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [onClose]);

  // Fetch user files when modal opens
  useEffect(() => {
    if (!isOpen) return;
    const fetchFiles = async () => {
      if (!userId) return;
      setFetchingFiles(true);
      try {
        const resp = await fetch('http://localhost:8080/api/files', {
          headers: { 'X-User-ID': userId },
        });
        if (resp.ok) {
          const data = await resp.json();
          setFiles(data.files || []);
        }
      } catch (_) {
        /* ignore */
      } finally {
        setFetchingFiles(false);
      }
    };
    fetchFiles();
  }, [isOpen, userId]);

  if (!isOpen) return null;

  const placeholderUuid = '00000000-0000-0000-0000-000000000000';

  const handleGenerate = async (input: string) => {
    setLoading(true);
    setError(null);
    try {
      const resp = await fetch('http://localhost:8080/api/ai/quiz', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          content: input,
          userId: placeholderUuid,
          questionCount: 5,
        }),
      });
      if (resp.ok) {
        const data: QuizDto = await resp.json();
        setQuiz(data);
        setAnswers({});
        setSubmitted(false);
      } else {
        const err = await resp.json();
        setError(err.error ?? 'Failed to generate quiz');
      }
    } catch (_) {
      setError('Network error');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateFromText = () => {
    if (!content.trim()) {
      setError('Please enter some text');
      return;
    }
    handleGenerate(content);
  };

  const handleGenerateFromFiles = async () => {
    if (files.length === 0) {
      setError('No files uploaded');
      return;
    }

    const texts: string[] = [];
    for (const file of files) {
      const lower = file.originalFilename.toLowerCase();
      const endpoint = lower.endsWith('.pdf')
        ? `http://localhost:8080/api/files/text/${file.storedFilename}`
        : `http://localhost:8080/api/files/${file.storedFilename}`;
      try {
        const resp = await fetch(endpoint, {
          headers: { 'X-User-ID': userId },
        });
        if (resp.ok) {
          const txt = await resp.text();
          texts.push(txt);
        }
      } catch (_) {
        /* ignore */
      }
    }

    if (texts.length === 0) {
      setError('No text content extracted from files');
      return;
    }
    handleGenerate(texts.join('\n\n'));
  };

  const selectAnswer = (questionId: string, option: number) => {
    if (submitted) return; // lock further changes
    setAnswers((prev) => ({ ...prev, [questionId]: option }));
  };

  const handleSubmit = () => {
    if (!quiz) return;
    if (Object.keys(answers).length < quiz.questions.length) {
      setError('Please answer all questions before submitting.');
      return;
    }
    setSubmitted(true);
  };

  const score = submitted
    ? quiz
      ? quiz.questions.filter((q) => answers[q.id] === q.correctOption).length
      : 0
    : 0;

  return (
    <div className="fixed inset-0 z-50 flex flex-col bg-black/70 backdrop-blur-sm">
      <div className="flex-1 overflow-auto flex flex-col items-center p-4">
        <div className="absolute top-4 right-4">
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-6 w-6" />
          </Button>
        </div>

        {error && (
          <Alert variant="destructive" className="mb-4">
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {/* Generation UI */}
        {!quiz && (
          <div className="space-y-4 w-full max-w-2xl">
            <textarea
              className="w-full min-h-[100px] rounded-md border border-input bg-background p-3 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Enter or paste text to generate quiz from..."
            />
            <div className="flex flex-wrap gap-2 justify-end">
              <Button onClick={handleGenerateFromText} disabled={loading}>
                {loading && <RefreshCw className="h-4 w-4 animate-spin" />}
                <AlignLeft className="h-4 w-4" />
                Generate From Text
              </Button>
              <Button variant="secondary" onClick={handleGenerateFromFiles} disabled={loading || fetchingFiles}>
                {(loading || fetchingFiles) && <RefreshCw className="h-4 w-4 animate-spin" />}
                <FolderOpen className="h-4 w-4" />
                Generate From Uploaded Files
              </Button>
            </div>
          </div>
        )}

        {/* Quiz display */}
        {quiz && (
          <div className="w-full max-w-3xl space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>{quiz.title}</CardTitle>
              </CardHeader>
              <CardContent className="space-y-6">
                {quiz.questions.map((q, idx) => {
                  const selected = answers[q.id];
                  const correct = submitted ? q.correctOption : null;

                  const renderOption = (label: string, optionNum: number, text: string) => {
                    const isSelected = selected === optionNum;
                    const isCorrect = submitted && correct === optionNum;
                    const isWrongSelection = submitted && isSelected && !isCorrect;

                    let variant: any = 'outline';
                    if (submitted) {
                      if (isCorrect) variant = 'default';
                      else if (isWrongSelection) variant = 'destructive';
                    } else if (isSelected) {
                      variant = 'secondary';
                    }

                    return (
                      <Button
                        key={optionNum}
                        variant={variant}
                        className="w-full justify-start"
                        onClick={() => selectAnswer(q.id, optionNum)}
                      >
                        <span className="font-bold mr-2">{label}.</span> {text}
                      </Button>
                    );
                  };

                  return (
                    <div key={q.id} className="space-y-3">
                      <p className="font-medium">
                        {idx + 1}. {q.question}
                      </p>
                      <div className="grid gap-2">
                        {renderOption('A', 1, q.optionA)}
                        {renderOption('B', 2, q.optionB)}
                        {renderOption('C', 3, q.optionC)}
                        {renderOption('D', 4, q.optionD)}
                      </div>
                    </div>
                  );
                })}

                {!submitted && (
                  <div className="flex justify-end">
                    <Button onClick={handleSubmit}>Submit Answers</Button>
                  </div>
                )}

                {submitted && (
                  <div className="flex items-center justify-between pt-4 border-t">
                    <p className="text-sm font-medium">
                      Your Score: {score} / {quiz.questions.length}
                    </p>
                    <div className="flex gap-2">
                      <Button variant="secondary" onClick={() => setQuiz(null)}>
                        New Quiz
                      </Button>
                      <Button onClick={() => {
                        setSubmitted(false);
                        setAnswers({});
                      }}>
                        Retake
                      </Button>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        )}
      </div>
    </div>
  );
};

export default QuizModal; 