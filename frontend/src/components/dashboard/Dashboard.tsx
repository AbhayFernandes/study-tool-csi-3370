import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Users, Activity, BookOpen, FileText } from 'lucide-react';
import { FileUpload, FileManager } from '../files';
import SummaryWidget from '../summary/SummaryWidget';
import FlashcardWidget from '../flashcards/FlashcardWidget';
import QuizWidget from '../quiz/QuizWidget';

interface User {
  username: string;
  token: string;
}

interface DashboardProps {
  user?: User;
}

const Dashboard: React.FC<DashboardProps> = ({ user }) => {
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleUploadComplete = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <main className="w-full min-h-screen bg-background px-4 py-8">
      <div className="max-w-7xl mx-auto space-y-8">
      {/* File Management Section */}
      <div className="grid gap-4 lg:grid-cols-2">
        <FileUpload 
          userId={user?.username || "anonymous"} 
          onUploadComplete={handleUploadComplete}
        />
        <FileManager 
          userId={user?.username || "anonymous"} 
          refreshTrigger={refreshTrigger}
        />
      </div>

      {/* Summarization Section */}
      <SummaryWidget 
        userId={user?.username || "anonymous"} 
        refreshTrigger={refreshTrigger}
      />

      <FlashcardWidget userId={user?.username || 'anonymous'} />
      <QuizWidget userId={user?.username || 'anonymous'} />
    </div>
  </main>
  );
};

export default Dashboard; 