import React from 'react';
import { cn } from '../../utils/cn';

export type BadgeVariant =
  | 'urgent'
  | 'warning'
  | 'active'
  | 'success'
  | 'neutral'
  | 'mono'
  | 'red'
  | 'yellow'
  | 'blue'
  | 'green'
  | 'gray'
  | 'purple';

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
    urgent: 'bg-rose-500/10 text-rose-400 border border-rose-500/30',
    red: 'bg-rose-500/10 text-rose-400 border border-rose-500/30',
    warning: 'bg-amber-500/10 text-amber-400 border border-amber-500/30',
    yellow: 'bg-amber-500/10 text-amber-400 border border-amber-500/30',
    active: 'bg-indigo-500/10 text-indigo-400 border border-indigo-500/30',
    blue: 'bg-indigo-500/10 text-indigo-400 border border-indigo-500/30',
    success: 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30',
    green: 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30',
    neutral: 'bg-slate-500/10 text-slate-400 border border-slate-500/30',
    gray: 'bg-slate-800 text-slate-400 border border-slate-700/60',
    purple: 'bg-purple-500/10 text-purple-300 border border-purple-500/30',
    mono: 'bg-slate-800 text-slate-300 font-mono text-[10px] border border-slate-700',
  };

  const sizeStyles = {
    sm: 'text-[10px] px-2 py-0.5 rounded',
    md: 'text-xs px-2.5 py-1 rounded-md font-medium',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 font-medium select-none whitespace-nowrap',
        variantStyles[variant] || variantStyles.neutral,
        sizeStyles[size],
        className
      )}
      {...props}
    >
      {children}
    </span>
  );
};