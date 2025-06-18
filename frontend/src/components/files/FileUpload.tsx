import React, { useState, useRef, useCallback } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Progress } from '../ui/progress';
import { Badge } from '../ui/badge';
import { Alert, AlertDescription } from '../ui/alert';
import { Upload, File, X, CheckCircle, AlertCircle, FileText } from 'lucide-react';

interface FileUploadProps {
  userId?: string;
  onUploadComplete?: (files: UploadedFileInfo[]) => void;
}

interface UploadedFileInfo {
  filename: string;
  originalFilename: string;
  size: number;
}

interface FileUploadState {
  file: File | null;
  uploading: boolean;
  progress: number;
  success: boolean;
  error: string | null;
}

const FileUpload: React.FC<FileUploadProps> = ({ userId, onUploadComplete }) => {
  const [uploadState, setUploadState] = useState<FileUploadState>({
    file: null,
    uploading: false,
    progress: 0,
    success: false,
    error: null,
  });
  const [dragActive, setDragActive] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const resetUploadState = () => {
    setUploadState({
      file: null,
      uploading: false,
      progress: 0,
      success: false,
      error: null,
    });
  };

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const getFileIcon = (fileName: string) => {
    const extension = fileName.split('.').pop()?.toLowerCase();
    switch (extension) {
      case 'pdf':
        return <FileText className="h-4 w-4 text-red-500" />;
      case 'doc':
      case 'docx':
        return <FileText className="h-4 w-4 text-blue-500" />;
      case 'txt':
        return <FileText className="h-4 w-4 text-gray-500" />;
      default:
        return <File className="h-4 w-4 text-gray-500" />;
    }
  };

  const handleFiles = useCallback((files: FileList | null) => {
    if (!files || files.length === 0) return;
    
    const file = files[0];
    setUploadState(prev => ({
      ...prev,
      file,
      success: false,
      error: null,
    }));
  }, []);

  const handleDrag = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  }, []);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    handleFiles(e.dataTransfer.files);
  }, [handleFiles]);

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    handleFiles(e.target.files);
  }, [handleFiles]);

  const uploadFile = async () => {
    if (!uploadState.file) return;

    setUploadState(prev => ({ ...prev, uploading: true, progress: 0, error: null }));

    try {
      const formData = new FormData();
      formData.append('file', uploadState.file);

      const xhr = new XMLHttpRequest();

      xhr.upload.addEventListener('progress', (e) => {
        if (e.lengthComputable) {
          const progress = Math.round((e.loaded / e.total) * 100);
          setUploadState(prev => ({ ...prev, progress }));
        }
      });

      xhr.addEventListener('load', () => {
        if (xhr.status === 200) {
          const response = JSON.parse(xhr.responseText);
          setUploadState(prev => ({ 
            ...prev, 
            uploading: false, 
            success: true,
            progress: 100 
          }));
          
          if (onUploadComplete) {
            onUploadComplete([{
              filename: response.filename,
              originalFilename: response.originalFilename,
              size: response.size,
            }]);
          }
        } else {
          const errorResponse = JSON.parse(xhr.responseText);
          setUploadState(prev => ({ 
            ...prev, 
            uploading: false, 
            error: errorResponse.error || 'Upload failed',
            progress: 0
          }));
        }
      });

      xhr.addEventListener('error', () => {
        setUploadState(prev => ({ 
          ...prev, 
          uploading: false, 
          error: 'Network error occurred',
          progress: 0
        }));
      });

      xhr.open('POST', 'http://localhost:8080/api/files/upload');
      
      // Add user ID header if available
      if (userId) {
        xhr.setRequestHeader('X-User-ID', userId);
      }

      xhr.send(formData);
    } catch (error) {
      setUploadState(prev => ({ 
        ...prev, 
        uploading: false, 
        error: 'An unexpected error occurred',
        progress: 0
      }));
    }
  };

  const removeFile = () => {
    setUploadState(prev => ({ ...prev, file: null, success: false, error: null }));
    if (inputRef.current) {
      inputRef.current.value = '';
    }
  };

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Upload className="h-5 w-5" />
          File Upload
        </CardTitle>
        <CardDescription>
          Upload documents and study materials to your personal storage
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {!uploadState.file ? (
          <div
            className={`border-2 border-dashed rounded-lg p-6 text-center transition-colors ${
              dragActive 
                ? 'border-primary bg-primary/10' 
                : 'border-gray-300 hover:border-gray-400'
            }`}
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
          >
            <Upload className="mx-auto h-12 w-12 text-gray-400 mb-4" />
            <div className="space-y-2">
              <p className="text-lg font-medium">
                Drag and drop your file here
              </p>
              <p className="text-sm text-muted-foreground">
                or click to browse files
              </p>
            </div>
            <Button 
              onClick={() => inputRef.current?.click()}
              variant="outline"
              className="mt-4"
            >
              Browse Files
            </Button>
            <input
              ref={inputRef}
              type="file"
              className="hidden"
              onChange={handleChange}
              accept=".pdf,.doc,.docx,.txt,.png,.jpg,.jpeg"
            />
          </div>
        ) : (
          <div className="space-y-4">
            <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
              <div className="flex items-center gap-3">
                {getFileIcon(uploadState.file.name)}
                <div>
                  <p className="text-sm font-medium">{uploadState.file.name}</p>
                  <p className="text-xs text-muted-foreground">
                    {formatFileSize(uploadState.file.size)}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                {uploadState.success && (
                  <Badge variant="default" className="bg-green-500">
                    <CheckCircle className="h-3 w-3 mr-1" />
                    Uploaded
                  </Badge>
                )}
                {!uploadState.uploading && !uploadState.success && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={removeFile}
                  >
                    <X className="h-4 w-4" />
                  </Button>
                )}
              </div>
            </div>

            {uploadState.uploading && (
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span>Uploading...</span>
                  <span>{uploadState.progress}%</span>
                </div>
                <Progress value={uploadState.progress} />
              </div>
            )}

            {uploadState.error && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>{uploadState.error}</AlertDescription>
              </Alert>
            )}

            {uploadState.success && (
              <Alert className="border-green-200 bg-green-50 dark:border-green-800 dark:bg-green-950">
                <CheckCircle className="h-4 w-4 text-green-600" />
                <AlertDescription className="text-green-800 dark:text-green-200">
                  File uploaded successfully!
                </AlertDescription>
              </Alert>
            )}

            <div className="flex gap-2">
              {!uploadState.uploading && !uploadState.success && (
                <Button onClick={uploadFile} className="flex-1">
                  <Upload className="h-4 w-4 mr-2" />
                  Upload File
                </Button>
              )}
              {uploadState.success && (
                <Button onClick={resetUploadState} variant="outline" className="flex-1">
                  Upload Another File
                </Button>
              )}
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default FileUpload; 