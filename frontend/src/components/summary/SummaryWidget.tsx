import React, { useState, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '../ui/card';
import { Button } from '../ui/button';
import { Alert, AlertDescription } from '../ui/alert';
import { RefreshCw, AlignLeft, AlertCircle, FolderOpen } from 'lucide-react';

interface SummaryWidgetProps {
  userId?: string;
  /**
   * Incrementing number supplied by parent when files change. When this value changes
   * we re-fetch the user's file list so the "Summarize Uploaded Files" button works
   * immediately after an upload.
   */
  refreshTrigger?: number;
}

interface FileInfo {
  id: string;
  originalFilename: string;
  storedFilename: string;
  fileSize: number;
  uploadTime: string;
  userId: string;
}

const SummaryWidget: React.FC<SummaryWidgetProps> = ({ userId, refreshTrigger }) => {
  const [content, setContent] = useState('');
  const [summary, setSummary] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [files, setFiles] = useState<FileInfo[]>([]);
  const [fetchingFiles, setFetchingFiles] = useState(false);

  const placeholderUuid = '00000000-0000-0000-0000-000000000000';

  /* Fetch uploaded files on mount */
  useEffect(() => {
    if (!userId) return;
    const fetchFiles = async () => {
      setFetchingFiles(true);
      try {
        const response = await fetch('http://localhost:8080/api/files', {
          headers: { 'X-User-ID': userId },
        });
        if (response.ok) {
          const data = await response.json();
          setFiles(data.files || []);
        }
      } catch (_) {
        /* silently ignore */
      } finally {
        setFetchingFiles(false);
      }
    };
    fetchFiles();
  }, [userId, refreshTrigger]);

  const handleSummarize = async () => {
    if (!content.trim()) {
      setError('Please enter some content to summarize.');
      return;
    }

    setLoading(true);
    setError(null);
    setSummary(null);

    try {
      const response = await fetch('http://localhost:8080/api/ai/summarize', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          content,
          userId: placeholderUuid,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        setSummary(data.summary || 'No summary returned.');
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to generate summary.');
      }
    } catch (err) {
      setError('Network error occurred while summarizing.');
    } finally {
      setLoading(false);
    }
  };

  const handleSummarizeFiles = async () => {
    if (files.length === 0) {
      setError('No uploaded files found to summarize.');
      return;
    }

    setLoading(true);
    setError(null);
    setSummary(null);

    try {
      // Fetch content of each .txt file
      const texts: string[] = [];
      for (const file of files) {
        const lowerName = file.originalFilename.toLowerCase();
        if (!(lowerName.endsWith('.txt') || lowerName.endsWith('.pdf'))) continue;

        const endpoint = lowerName.endsWith('.pdf')
          ? `http://localhost:8080/api/files/text/${file.storedFilename}`
          : `http://localhost:8080/api/files/${file.storedFilename}`;

        const resp = await fetch(endpoint, {
          headers: { 'X-User-ID': userId || 'anonymous' },
        });
        if (resp.ok) {
          const txt = await resp.text();
          texts.push(txt);
        }
      }

      if (texts.length === 0) {
        setError('No text-based files available for summarization.');
        return;
      }

      const combinedContent = texts.join('\n\n');

      const response = await fetch('http://localhost:8080/api/ai/summarize', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          content: combinedContent,
          userId: placeholderUuid,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        setSummary(data.summary || 'No summary returned.');
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to generate summary.');
      }
    } catch (_) {
      setError('Network error occurred while summarizing.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <AlignLeft className="h-5 w-5" />
          Summarize Material
        </CardTitle>
        <CardDescription>
          Paste text below and get a concise summary powered by AI
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {error && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Enter or paste your study material here..."
          className="w-full min-h-[120px] rounded-md border border-input bg-background p-3 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
        />
        <div className="flex flex-wrap items-center justify-end gap-2">
          <Button onClick={handleSummarize} disabled={loading}>
            {loading && <RefreshCw className="h-4 w-4 animate-spin" />}
            Summarize Text
          </Button>
          <Button
            variant="secondary"
            onClick={handleSummarizeFiles}
            disabled={loading || fetchingFiles}
          >
            {loading && <RefreshCw className="h-4 w-4 animate-spin" />}
            {fetchingFiles && !loading && <RefreshCw className="h-4 w-4 animate-spin" />}
            <FolderOpen className="h-4 w-4" />
            Summarize Uploaded Files
          </Button>
        </div>
        {summary && (
          <div className="prose dark:prose-invert max-w-none">
            <ReactMarkdown>{summary}</ReactMarkdown>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default SummaryWidget; 