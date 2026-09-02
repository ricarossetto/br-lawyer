import React, { useEffect } from 'react';
import { X } from 'lucide-react';
import { cn } from '../../utils/cn';

interface DrawerProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  subtitle?: string;
  children: React.ReactNode;
  width?: 'sm' | 'md' | 'lg' | 'xl';
}

export const Drawer: React.FC<DrawerProps> = ({
  isOpen,
  onClose,
  title,
  subtitle,
  children,
  width = 'lg',
}) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const widthStyles = {
    sm: 'max-w-md',
    md: 'max-w-lg',
    lg: 'max-w-2xl',
    xl: 'max-w-3xl',
  };

  return (
    <div className="fixed inset-0 z-40 overflow-hidden">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/80 transition-opacity"
        onClick={onClose}
      />

      <div className="fixed inset-y-0 right-0 flex pl-10 max-w-full">
        <div
          className={cn(
            'w-screen bg-[#0F0F0F] border-l border-[#262626] flex flex-col animate-drawer-in rounded-none',
            widthStyles[width]
          )}
        >
          {/* Header */}
          <div className="px-6 py-4 border-b border-[#262626] flex items-start justify-between bg-[#0A0A0A] shrink-0">
            <div>
              <h3 className="text-sm font-bold text-[#FAFAFA] font-heading tracking-tight">{title}</h3>
              {subtitle && <p className="text-xs text-[#737373] font-mono mt-0.5 tracking-wider">{subtitle}</p>}
            </div>
            <button
              onClick={onClose}
              className="p-1.5 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#1A1A1A] transition-colors"
              title="Fechar (Esc)"
            >
              <X className="h-4 w-4" />
            </button>
          </div>

          {/* Body */}
          <div className="flex-1 overflow-y-auto p-6">{children}</div>
        </div>
      </div>
    </div>
  );
};