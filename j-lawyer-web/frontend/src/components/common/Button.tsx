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
        'bg-gradient-to-r from-[#EA580C] to-[#F7931A] text-white font-semibold shadow-[0_0_20px_-5px_rgba(234,88,12,0.5)] hover:shadow-[0_0_28px_-4px_rgba(247,147,26,0.7)] hover:scale-[1.02] border border-[#F7931A]/40 focus:ring-2 focus:ring-[#F7931A]/50 active:scale-[0.98] rounded-full',
      secondary:
        'bg-[#0F1115] hover:bg-[#181B20] text-slate-200 border border-white/10 hover:border-[#F7931A]/40 hover:text-white focus:ring-2 focus:ring-[#F7931A]/30 rounded-full',
      outline:
        'bg-transparent hover:bg-white/5 text-slate-300 border border-white/20 hover:border-[#F7931A]/60 hover:text-[#F7931A] focus:ring-2 focus:ring-[#F7931A]/30 rounded-full',
      ghost:
        'bg-transparent hover:bg-white/5 text-slate-400 hover:text-[#F7931A] border-transparent rounded-full',
      danger:
        'bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 focus:ring-2 focus:ring-rose-500/40 rounded-full',
    };

    const sizeStyles: Record<ButtonSize, string> = {
      xs: 'h-7 px-2.5 text-xs rounded-full gap-1.5',
      sm: 'h-8 px-3.5 text-xs rounded-full gap-2 font-medium',
      md: 'h-9 px-4 text-sm rounded-full gap-2 font-medium',
      lg: 'h-10 px-6 text-sm rounded-full gap-2.5 font-semibold',
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