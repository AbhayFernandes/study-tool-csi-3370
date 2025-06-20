import React, { useEffect, useState } from 'react';
import { X, RefreshCw, FolderOpen, AlignLeft } from 'lucide-react';
import FlashcardCard from './FlashcardCard';
import { Button } from '../ui/button';
import { Alert, AlertDescription } from '../ui/alert';

export interface Flashcard {
  id: string;
  setId: string;
  front: string;
  back: string;
  createdAt: string;
}

interface FlashcardModalProps {
  userId: string;
  isOpen: boolean;
  onClose: () => void;
}

const FlashcardModal: React.FC<FlashcardModalProps> = ({ userId, isOpen, onClose }) => {
  const [flashcards, setFlashcards] = useState<Flashcard[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [content, setContent] = useState('');
  const [files, setFiles] = useState<any[]>([]);
  const [fetchingFiles, setFetchingFiles] = useState(false);

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

  const validUserId =
    userId && userId.length === 36 ? userId : '00000000-0000-0000-0000-000000000000';

  const handleGenerate = async (input: string) => {
    setLoading(true);
    setError(null);
    try {
      const resp = await fetch('http://localhost:8080/api/ai/flashcards', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          content: input,
          userId: validUserId,
          count: 5,
        }),
      });
      if (resp.ok) {
        const data: Flashcard[] = await resp.json();
        setFlashcards(data);
      } else {
        const err = await resp.json();
        setError(err.error ?? 'Failed to generate');
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

  return (
    <div className="fixed inset-0 z-50 flex flex-col bg-black/70 backdrop-blur-sm">
      <div className="flex-1 overflow-auto flex flex-col items-center justify-center p-4">
        {/* Header */}
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

        {flashcards.length === 0 && (
          <div className="space-y-4 w-full max-w-2xl">
            <textarea
              className="w-full min-h-[100px] rounded-md border border-input bg-background p-3 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Enter or paste text to generate flashcards from..."
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

        {flashcards.length > 0 && (
          <div className="flex flex-wrap gap-4 justify-center">
            {flashcards.map((fc) => (
              <FlashcardCard key={fc.id} front={fc.front} back={fc.back} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default FlashcardModal; 