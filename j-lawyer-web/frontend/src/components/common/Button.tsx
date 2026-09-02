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
        'bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A] font-bold border border-[#FF3D00] focus:ring-2 focus:ring-[#FF3D00] focus:ring-offset-2 focus:ring-offset-[#0A0A0A] active:translate-y-px rounded-none tracking-wider uppercase',
      secondary:
        'bg-[#1A1A1A] hover:bg-[#262626] text-[#FAFAFA] border border-[#262626] hover:border-[#737373] focus:ring-1 focus:ring-[#FF3D00] active:translate-y-px rounded-none tracking-wider uppercase',
      outline:
        'bg-transparent hover:bg-[#FAFAFA] text-[#FAFAFA] hover:text-[#0A0A0A] border border-[#FAFAFA] focus:ring-2 focus:ring-[#FF3D00] active:translate-y-px rounded-none tracking-wider uppercase transition-colors duration-150',
      ghost:
        'bg-transparent hover:bg-[#1A1A1A] text-[#737373] hover:text-[#FAFAFA] border border-transparent hover:border-[#262626] rounded-none tracking-wider uppercase',
      danger:
        'bg-transparent hover:bg-rose-950/30 text-rose-500 border border-rose-600/40 hover:border-rose-500 active:translate-y-px rounded-none tracking-wider uppercase',
    };

    const sizeStyles: Record<ButtonSize, string> = {
      xs: 'h-7 px-3 text-[10px] font-mono gap-1.5',
      sm: 'h-8 px-4 text-xs font-semibold gap-2',
      md: 'h-10 px-6 text-xs font-semibold gap-2.5',
      lg: 'h-12 px-8 text-sm font-bold gap-3',
    };

    return (
      <button
        ref={ref}
        disabled={disabled || isLoading}
        className={cn(
          'inline-flex items-center justify-center transition-all duration-150 outline-none select-none disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer whitespace-nowrap',
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
        <span>{children}</span>
        {!isLoading && rightIcon}
      </button>
    );
  }
);
Button.displayName = 'Button';