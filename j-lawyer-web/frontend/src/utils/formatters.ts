import { format, isValid } from 'date-fns';
import { ptBR } from 'date-fns/locale';

/**
 * Formats a CNJ process number (NPU) into standard mask: NNNNNNN-DD.AAAA.J.TR.OOOO
 */
export function formatCNJ(npu: string | undefined | null): string {
  if (!npu) return '—';
  const clean = npu.replace(/\D/g, '');
  if (clean.length === 20) {
    return `${clean.substring(0, 7)}-${clean.substring(7, 9)}.${clean.substring(9, 13)}.${clean.substring(13, 14)}.${clean.substring(14, 16)}.${clean.substring(16, 20)}`;
  }
  return npu;
}

/**
 * Formats epoch timestamp into Brazilian date (dd/MM/yyyy)
 */
export function formatDate(timestamp: number | string | Date | undefined | null): string {
  if (!timestamp) return '—';
  const date = typeof timestamp === 'number' ? new Date(timestamp) : new Date(timestamp);
  if (!isValid(date)) return '—';
  return format(date, 'dd/MM/yyyy', { locale: ptBR });
}

/**
 * Formats epoch timestamp into Brazilian date & time (dd/MM/yyyy HH:mm)
 */
export function formatDateTime(timestamp: number | string | Date | undefined | null): string {
  if (!timestamp) return '—';
  const date = typeof timestamp === 'number' ? new Date(timestamp) : new Date(timestamp);
  if (!isValid(date)) return '—';
  return format(date, 'dd/MM/yyyy HH:mm', { locale: ptBR });
}

/**
 * Formats monetary amounts into Brazilian Real (BRL)
 */
export function formatBRL(amount: number | string | undefined | null): string {
  if (amount === undefined || amount === null || amount === '') return 'R$ 0,00';
  const num = typeof amount === 'string' ? parseFloat(amount.replace(',', '.')) : amount;
  if (isNaN(num)) return 'R$ 0,00';
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(num);
}