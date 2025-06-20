import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '../ui/card';
import { Button } from '../ui/button';
import { BookOpen } from 'lucide-react';
import QuizModal from './QuizModal';

interface QuizWidgetProps {
  userId: string;
}

const QuizWidget: React.FC<QuizWidgetProps> = ({ userId }) => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <Card className="w-full">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <BookOpen className="h-5 w-5" />
            Generate Quiz
          </CardTitle>
          <CardDescription>
            Create an AI-generated multiple-choice quiz from your study material
          </CardDescription>
        </CardHeader>
        <CardContent className="flex justify-end">
          <Button onClick={() => setOpen(true)}>Open Quiz Builder</Button>
        </CardContent>
      </Card>

      <QuizModal isOpen={open} onClose={() => setOpen(false)} userId={userId} />
    </>
  );
};

export default QuizWidget; 