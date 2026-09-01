import React from 'react';
import { cn } from '../../utils/cn';

export type BadgeVariant = 'urgent' | 'warning' | 'active' | 'success' | 'neutral' | 'mono';

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant;
  size?: 'sm' | 'md';
  children: React.ReactNode;
}

export const Badge: React.FC<BadgeProps> = ({
  variant = 'neutral',
  size = 'sm',
  className,
  children,
  ...props
}) => {
  const variantStyles: Record<BadgeVariant, string> = {
    urgent: 'bg-red-500/10 text-red-400 border border-red-500/30',
    warning: 'bg-amber-500/10 text-amber-400 border border-amber-500/30',
    active: 'bg-indigo-500/10 text-indigo-400 border border-indigo-500/30',
    success: 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30',
    neutral: 'bg-slate-500/10 text-slate-400 border border-slate-500/30',
    mono: 'bg-slate-800 text-slate-300 font-mono text-xs border border-slate-700',
  };

  const sizeStyles = {
    sm: 'text-[11px] px-2 py-0.5 rounded',
    md: 'text-xs px-2.5 py-1 rounded-md font-medium',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 font-medium select-none whitespace-nowrap',
        variantStyles[variant],
        sizeStyles[size],
        className
      )}
      {...props}
    >
      {children}
    </span>
  );
};