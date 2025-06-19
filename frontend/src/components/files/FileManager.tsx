import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Alert, AlertDescription } from '../ui/alert';
import { FileText, Download, Trash2, RefreshCw, AlertCircle, File, FolderOpen } from 'lucide-react';

interface FileManagerProps {
  userId?: string;
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

const FileManager: React.FC<FileManagerProps> = ({ userId, refreshTrigger }) => {
  const [files, setFiles] = useState<FileInfo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deletingFile, setDeletingFile] = useState<string | null>(null);

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const getFileIcon = (fileName: string) => {
    if (!fileName) return <File className="h-4 w-4 text-gray-500" />;
    const extension = fileName.split('.').pop()?.toLowerCase();
    switch (extension) {
      case 'pdf':
        return <FileText className="h-4 w-4 text-red-500" />;
      case 'doc':
      case 'docx':
        return <FileText className="h-4 w-4 text-blue-500" />;
      case 'txt':
        return <FileText className="h-4 w-4 text-gray-500" />;
      case 'png':
      case 'jpg':
      case 'jpeg':
        return <File className="h-4 w-4 text-green-500" />;
      default:
        return <File className="h-4 w-4 text-gray-500" />;
    }
  };

  const getFileTypeColor = (fileName: string): "default" | "secondary" | "destructive" | "outline" => {
    if (!fileName) return 'secondary';
    const extension = fileName.split('.').pop()?.toLowerCase();
    switch (extension) {
      case 'pdf':
        return 'destructive';
      case 'doc':
      case 'docx':
        return 'default';
      case 'txt':
        return 'secondary';
      case 'png':
      case 'jpg':
      case 'jpeg':
        return 'outline';
      default:
        return 'secondary';
    }
  };

  const fetchFiles = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const headers: Record<string, string> = {};
      if (userId) {
        headers['X-User-ID'] = userId;
      }

      const response = await fetch('http://localhost:8080/api/files', {
        method: 'GET',
        headers,
      });

      if (response.ok) {
        const data = await response.json();
        setFiles(data.files || []);
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to fetch files');
      }
    } catch (error) {
      setError('Network error occurred');
    } finally {
      setLoading(false);
    }
  };

  const downloadFile = async (file: FileInfo) => {
    try {
      const headers: Record<string, string> = {};
      if (userId) {
        headers['X-User-ID'] = userId;
      }

      const response = await fetch(`http://localhost:8080/api/files/${file.storedFilename}`, {
        method: 'GET',
        headers,
      });

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = file.originalFilename; // Use original filename for download
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to download file');
      }
    } catch (error) {
      setError('Network error occurred during download');
    }
  };

  const deleteFile = async (file: FileInfo) => {
    setDeletingFile(file.storedFilename);
    
    try {
      const headers: Record<string, string> = {};
      if (userId) {
        headers['X-User-ID'] = userId;
      }

      const response = await fetch(`http://localhost:8080/api/files/${file.storedFilename}`, {
        method: 'DELETE',
        headers,
      });

      if (response.ok) {
        setFiles(prev => prev.filter(f => f.id !== file.id));
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to delete file');
      }
    } catch (error) {
      setError('Network error occurred during deletion');
    } finally {
      setDeletingFile(null);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, [userId, refreshTrigger]);

  if (loading && files.length === 0) {
    return (
      <Card className="w-full">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FolderOpen className="h-5 w-5" />
            My Files
          </CardTitle>
          <CardDescription>
            Manage your uploaded files and documents
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center py-8">
            <RefreshCw className="h-6 w-6 animate-spin text-muted-foreground" />
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <FolderOpen className="h-5 w-5" />
            My Files
            <Badge variant="secondary">{files.length}</Badge>
          </div>
          <Button
            variant="ghost"
            size="sm"
            onClick={fetchFiles}
            disabled={loading}
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </Button>
        </CardTitle>
        <CardDescription>
          Manage your uploaded files and documents
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {error && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {files.length === 0 ? (
          <div className="text-center py-8">
            <FolderOpen className="mx-auto h-12 w-12 text-gray-400 mb-4" />
            <p className="text-lg font-medium text-muted-foreground">No files uploaded</p>
            <p className="text-sm text-muted-foreground">
              Upload your first file to get started
            </p>
          </div>
        ) : (
          <div className="space-y-2">
            {files.map((file) => (
              <div
                key={file.id}
                className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
              >
                <div className="flex items-center gap-3 flex-1 min-w-0">
                  {getFileIcon(file.originalFilename)}
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium truncate">
                      {file.originalFilename}
                    </p>
                    <div className="flex items-center gap-2 mt-1">
                      <p className="text-xs text-muted-foreground">
                        {formatFileSize(file.fileSize)}
                      </p>
                      {file.uploadTime && (
                        <>
                          <span className="text-xs text-muted-foreground">•</span>
                          <p className="text-xs text-muted-foreground">
                            {new Date(file.uploadTime).toLocaleDateString()}
                          </p>
                        </>
                      )}
                    </div>
                  </div>
                  <Badge variant={getFileTypeColor(file.originalFilename)}>
                    {file.originalFilename.split('.').pop()?.toUpperCase() || 'FILE'}
                  </Badge>
                </div>
                <div className="flex items-center gap-1 ml-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => downloadFile(file)}
                    title="Download file"
                  >
                    <Download className="h-4 w-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => deleteFile(file)}
                    disabled={deletingFile === file.storedFilename}
                    title="Delete file"
                    className="text-destructive hover:text-destructive"
                  >
                    {deletingFile === file.storedFilename ? (
                      <RefreshCw className="h-4 w-4 animate-spin" />
                    ) : (
                      <Trash2 className="h-4 w-4" />
                    )}
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default FileManager; 