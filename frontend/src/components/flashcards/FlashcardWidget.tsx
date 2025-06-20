import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '../ui/card';
import { Button } from '../ui/button';
import { Layers3 } from 'lucide-react';
import FlashcardModal from './FlashcardModal';

interface FlashcardWidgetProps {
  userId: string;
  refreshTrigger?: number; // unused for now – future file list refresh
}

const FlashcardWidget: React.FC<FlashcardWidgetProps> = ({ userId }) => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <Card className="w-full">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Layers3 className="h-5 w-5" />
            Generate Flashcards
          </CardTitle>
          <CardDescription>
            Create AI-generated flashcards from your study material
          </CardDescription>
        </CardHeader>
        <CardContent className="flex justify-end">
          <Button onClick={() => setOpen(true)}>Open Flashcard Builder</Button>
        </CardContent>
      </Card>

      <FlashcardModal isOpen={open} onClose={() => setOpen(false)} userId={userId} />
    </>
  );
};

export default FlashcardWidget; 