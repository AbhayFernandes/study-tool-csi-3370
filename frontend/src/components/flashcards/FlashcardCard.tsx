import React, { useState } from 'react';

interface FlashcardCardProps {
  front: string;
  back: string;
}

const FlashcardCard: React.FC<FlashcardCardProps> = ({ front, back }) => {
  const [flipped, setFlipped] = useState(false);

  return (
    <div
      className="rounded-xl border p-6 bg-background text-white shadow-md cursor-pointer w-80 h-48 flex items-center justify-center text-center select-none overflow-auto"
      onClick={() => setFlipped(!flipped)}
    >
      <span className="text-sm whitespace-pre-wrap">{flipped ? back : front}</span>
    </div>
  );
};

export default FlashcardCard; 