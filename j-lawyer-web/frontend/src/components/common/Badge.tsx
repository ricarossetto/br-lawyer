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
    urgent: 'bg-rose-950/30 text-rose-400 border border-rose-600/40',
    red: 'bg-rose-950/30 text-rose-400 border border-rose-600/40',
    warning: 'bg-amber-950/30 text-amber-400 border border-amber-600/40',
    yellow: 'bg-amber-950/30 text-amber-400 border border-amber-600/40',
    active: 'bg-[#FF3D00]/15 text-[#FF3D00] border border-[#FF3D00]/40',
    blue: 'bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626]',
    success: 'bg-emerald-950/30 text-emerald-400 border border-emerald-600/40',
    green: 'bg-emerald-950/30 text-emerald-400 border border-emerald-600/40',
    neutral: 'bg-[#141414] text-[#737373] border border-[#262626]',
    gray: 'bg-[#141414] text-[#737373] border border-[#262626]',
    purple: 'bg-[#FF3D00]/15 text-[#FF3D00] border border-[#FF3D00]/40',
    mono: 'bg-[#141414] text-[#FAFAFA] border border-[#262626]',
  };

  const sizeStyles = {
    sm: 'text-[10px] px-2 py-0.5 rounded-none font-mono tracking-wider uppercase',
    md: 'text-xs px-2.5 py-1 rounded-none font-mono tracking-wider uppercase font-semibold',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 select-none whitespace-nowrap',
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