import React from 'react';
import { cn } from '../../utils/cn';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  error?: string;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ leftIcon, rightIcon, error, className, ...props }, ref) => {
    return (
      <div className="relative w-full">
        {leftIcon && (
          <div className="absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none flex items-center">
            {leftIcon}
          </div>
        )}
        <input
          ref={ref}
          className={cn(
            'w-full h-8 bg-[#0F1115] border border-white/10 rounded-lg text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-[#F7931A] focus:ring-1 focus:ring-[#F7931A]/40 focus:shadow-[0_0_15px_-3px_rgba(247,147,26,0.3)] transition-all',
            leftIcon ? 'pl-8' : 'pl-3',
            rightIcon ? 'pr-8' : 'pr-3',
            error && 'border-red-500 focus:border-red-500 focus:ring-red-500/50',
            className
          )}
          {...props}
        />
        {rightIcon && (
          <div className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 flex items-center">
            {rightIcon}
          </div>
        )}
        {error && <p className="mt-1 text-[11px] text-red-400">{error}</p>}
      </div>
    );
  }
);
Input.displayName = 'Input';