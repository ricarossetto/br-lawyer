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
            'w-full h-9 bg-[#1A1A1A] border border-[#262626] rounded-none text-xs text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors duration-150',
            leftIcon ? 'pl-8' : 'pl-3',
            rightIcon ? 'pr-8' : 'pr-3',
            error && 'border-rose-500 focus:border-rose-500',
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