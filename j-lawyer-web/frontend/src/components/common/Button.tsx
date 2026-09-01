import React from 'react';
import { cn } from '../../utils/cn';

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger' | 'outline';
export type ButtonSize = 'xs' | 'sm' | 'md' | 'lg';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  isLoading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'secondary',
      size = 'sm',
      isLoading = false,
      leftIcon,
      rightIcon,
      className,
      children,
      disabled,
      ...props
    },
    ref
  ) => {
    const variantStyles: Record<ButtonVariant, string> = {
      primary:
        'bg-indigo-600 hover:bg-indigo-500 text-white font-medium shadow-sm shadow-indigo-950 border border-indigo-500/30 focus:ring-2 focus:ring-indigo-500/50',
      secondary:
        'bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 focus:ring-2 focus:ring-slate-600',
      outline:
        'bg-transparent hover:bg-slate-800 text-slate-300 border border-slate-700 focus:ring-2 focus:ring-slate-600',
      ghost:
        'bg-transparent hover:bg-slate-800/60 text-slate-400 hover:text-slate-200 border-transparent',
      danger:
        'bg-red-600/20 hover:bg-red-600/30 text-red-300 border border-red-500/40 focus:ring-2 focus:ring-red-500/50',
    };

    const sizeStyles: Record<ButtonSize, string> = {
      xs: 'h-7 px-2 text-xs rounded gap-1.5',
      sm: 'h-8 px-3 text-xs rounded-md gap-2 font-medium',
      md: 'h-9 px-4 text-sm rounded-md gap-2 font-medium',
      lg: 'h-10 px-5 text-sm rounded-lg gap-2.5 font-semibold',
    };

    return (
      <button
        ref={ref}
        disabled={disabled || isLoading}
        className={cn(
          'inline-flex items-center justify-center transition-all duration-150 outline-none select-none disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer',
          variantStyles[variant],
          sizeStyles[size],
          className
        )}
        {...props}
      >
        {isLoading ? (
          <svg className="animate-spin h-3.5 w-3.5 text-current" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
          </svg>
        ) : (
          leftIcon
        )}
        {children}
        {!isLoading && rightIcon}
      </button>
    );
  }
);
Button.displayName = 'Button';