import React, { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Calendar as CalendarIcon,
  ChevronLeft,
  ChevronRight,
  Clock,
  Plus,
  Filter,
  AlertTriangle,
  Scale,
  CheckCircle2,
  MapPin,
  User,
} from 'lucide-react';
import { calendarService } from '../../api/calendarService';
import { RestfulCalendarEventV8 } from '../../types/calendar';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { cn } from '../../utils/cn';
import { formatCNJ } from '../../utils/formatters';

interface CalendarViewProps {
  onSelectCase?: (caseId: string) => void;
}

export const CalendarView: React.FC<CalendarViewProps> = ({ onSelectCase }) => {
  const [currentDate, setCurrentDate] = useState(new Date(2026, 8, 1)); // Set to Sept 2026 for mock data
  const [viewMode, setViewMode] = useState<'month' | 'list'>('month');
  const [filterType, setFilterType] = useState<string>('all');
  const [selectedEvent, setSelectedEvent] = useState<RestfulCalendarEventV8 | null>(null);

  const { data: events = [], isLoading } = useQuery({
    queryKey: ['calendar-events', filterType],
    queryFn: async () => {
      try {
        return await calendarService.getEvents();
      } catch {
        return [
          {
            id: 'evt-001',
            summary: 'Prazo Fatal: Réplica à Contestação da União Federal (15 dias úteis - Art. 350 CPC)',
            start: new Date(2026, 8, 16, 18, 0).getTime(),
            type: 'respite' as const,
            done: false,
            caseId: '5001234-56.2026.4.04.7105',
            caseNumber: '5001234-56.2026.4.04.7105',
            caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
            assignee: 'admin',
          },
          {
            id: 'evt-002',
            summary: 'Audiência de Conciliação e Instrução - 2ª Vara Federal',
            start: new Date(2026, 8, 22, 14, 30).getTime(),
            end: new Date(2026, 8, 22, 16, 0).getTime(),
            type: 'event' as const,
            done: false,
            caseId: '5001234-56.2026.4.04.7105',
            caseNumber: '5001234-56.2026.4.04.7105',
            caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
            location: 'Sala de Audiências Virtual - TRF4 / Teams',
            assignee: 'admin',
          },
          {
            id: 'evt-003',
            summary: 'Diligência: Juntada de Procuração Atualizada e Contrato Social',
            start: new Date(2026, 7, 30, 18, 0).getTime(),
            type: 'followup' as const,
            done: false,
            caseId: 'BR-2026/0001',
            caseNumber: 'BR-2026/0001',
            caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
            assignee: 'admin',
          },
        ];
      }
    },
  });

  const nextMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1));
  };

  const prevMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1));
  };

  const monthNames = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro',
  ];

  const daysOfWeek = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];

  // Calendar matrix generator
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();
  const firstDayIndex = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();

  const daysArray = [];
  for (let i = 0; i < firstDayIndex; i++) {
    daysArray.push(null);
  }
  for (let d = 1; d <= daysInMonth; d++) {
    daysArray.push(new Date(year, month, d));
  }

  const getEventsForDay = (date: Date) => {
    return events.filter((e) => {
      const eDate = new Date(e.start);
      return (
        eDate.getDate() === date.getDate() &&
        eDate.getMonth() === date.getMonth() &&
        eDate.getFullYear() === date.getFullYear()
      );
    });
  };

  return (
    <div className="space-y-4 font-sans">
      {/* Control Ribbon */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3 bg-surface border border-border p-5 rounded-none">
        {/* Month Navigation */}
        <div className="flex items-center gap-3">
          <div className="flex items-center border border-border bg-bg">
            <button
              onClick={prevMonth}
              className="p-2 hover:bg-elevated text-muted-fg hover:text-fg transition-colors cursor-pointer"
            >
              <ChevronLeft className="h-4 w-4" />
            </button>
            <span className="px-4 text-xs font-mono font-bold uppercase tracking-wider text-fg min-w-[150px] text-center">
              {monthNames[month]} {year}
            </span>
            <button
              onClick={nextMonth}
              className="p-2 hover:bg-elevated text-muted-fg hover:text-fg transition-colors cursor-pointer"
            >
              <ChevronRight className="h-4 w-4" />
            </button>
          </div>

          {/* CPC 2015 Recess Banner Note */}
          <div className="hidden lg:flex items-center gap-1.5 px-3 py-1 bg-elevated border border-border text-[10px] font-mono text-muted-fg">
            <Scale className="h-3.5 w-3.5 text-accent" />
            <span>Contagem Art. 219 CPC (Dias Úteis) • Recesso Forense: 20/Dez a 20/Jan (Art. 220 CPC)</span>
          </div>
        </div>

        {/* View Mode Switcher & New Event */}
        <div className="flex items-center gap-2.5">
          <div className="flex items-center bg-bg p-1 border border-border">
            <button
              onClick={() => setViewMode('month')}
              className={cn(
                'px-3 py-1 text-[10px] font-mono uppercase tracking-wider transition-colors cursor-pointer',
                viewMode === 'month' ? 'bg-elevated text-fg font-bold' : 'text-muted-fg hover:text-fg'
              )}
            >
              Grade Mensal
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={cn(
                'px-3 py-1 text-[10px] font-mono uppercase tracking-wider transition-colors cursor-pointer',
                viewMode === 'list' ? 'bg-elevated text-fg font-bold' : 'text-muted-fg hover:text-fg'
              )}
            >
              Lista Cronológica
            </button>
          </div>

          <Button variant="primary" size="sm" leftIcon={<Plus className="h-3.5 w-3.5" />}>
            Novo Compromisso / Prazo
          </Button>
        </div>
      </div>

      {/* Main Calendar View */}
      {viewMode === 'month' ? (
        <div className="bg-surface border border-border rounded-none overflow-hidden">
          {/* Days of week header */}
          <div className="grid grid-cols-7 border-b border-border bg-bg text-center font-mono text-[10px] font-bold text-muted-fg uppercase tracking-wider">
            {daysOfWeek.map((day, idx) => (
              <div key={day} className={cn('py-2.5 border-r border-border last:border-r-0', idx === 0 || idx === 6 ? 'text-rose-400/80' : '')}>
                {day}
              </div>
            ))}
          </div>

          {/* Calendar Grid */}
          <div className="grid grid-cols-7 auto-rows-fr bg-border gap-[1px]">
            {daysArray.map((day, idx) => {
              if (!day) {
                return <div key={`empty-${idx}`} className="bg-surface min-h-[110px] opacity-40" />;
              }

              const isToday =
                day.getDate() === new Date().getDate() &&
                day.getMonth() === new Date().getMonth() &&
                day.getFullYear() === new Date().getFullYear();
              const dayEvents = getEventsForDay(day);
              const isWeekend = day.getDay() === 0 || day.getDay() === 6;

              return (
                <div
                  key={day.toISOString()}
                  className={cn(
                    'bg-surface min-h-[110px] p-2 flex flex-col justify-between transition-colors hover:bg-elevated group',
                    isToday ? 'border-2 border-accent' : ''
                  )}
                >
                  <div className="flex items-center justify-between">
                    <span
                      className={cn(
                        'text-xs font-mono font-bold px-1.5 py-0.5',
                        isToday ? 'bg-accent text-accent-fg' : isWeekend ? 'text-rose-400/70' : 'text-fg'
                      )}
                    >
                      {day.getDate()}
                    </span>
                    {dayEvents.length > 0 && (
                      <span className="text-[9px] font-mono text-accent font-bold">
                        {dayEvents.length} {dayEvents.length === 1 ? 'item' : 'itens'}
                      </span>
                    )}
                  </div>

                  {/* Event pills inside day cell */}
                  <div className="space-y-1 mt-1 flex-1 overflow-hidden">
                    {dayEvents.map((evt) => (
                      <div
                        key={evt.id}
                        onClick={() => setSelectedEvent(evt)}
                        className={cn(
                          'p-1.5 text-[10px] font-mono truncate border cursor-pointer transition-colors',
                          evt.type === 'respite'
                            ? 'bg-rose-950/20 text-rose-300 border-rose-800/40 hover:border-rose-500 font-bold'
                            : evt.type === 'event'
                            ? 'bg-amber-950/20 text-amber-300 border-amber-800/40 hover:border-amber-500'
                            : 'bg-elevated text-fg border-border hover:border-muted-fg'
                        )}
                        title={evt.summary}
                      >
                        {evt.summary}
                      </div>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      ) : (
        /* List View */
        <div className="bg-surface border border-border rounded-none overflow-hidden">
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="border-b border-border bg-bg text-[10px] font-bold text-muted-fg uppercase tracking-wider font-mono">
                <th className="py-3 px-4">Data & Horário</th>
                <th className="py-3 px-4">Tipo</th>
                <th className="py-3 px-4">Descrição do Prazo / Audiência</th>
                <th className="py-3 px-4">Processo Vinculado</th>
                <th className="py-3 px-4">Responsável</th>
                <th className="py-3 px-4 text-right">Ação</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {events.map((evt) => {
                const date = new Date(evt.start);
                return (
                  <tr
                    key={evt.id}
                    onClick={() => setSelectedEvent(evt)}
                    className="hover:bg-elevated transition-colors cursor-pointer group"
                  >
                    <td className="py-3 px-4 font-mono font-bold text-fg whitespace-nowrap">
                      {date.toLocaleDateString('pt-BR')} às {date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}
                    </td>
                    <td className="py-3 px-4 whitespace-nowrap">
                      <Badge variant={evt.type === 'respite' ? 'red' : evt.type === 'event' ? 'yellow' : 'mono'} size="sm">
                        {evt.type === 'respite' ? 'Prazo Fatal' : evt.type === 'event' ? 'Audiência' : 'Diligência'}
                      </Badge>
                    </td>
                    <td className="py-3 px-4 font-bold text-fg max-w-md truncate group-hover:text-accent">
                      {evt.summary}
                    </td>
                    <td className="py-3 px-4 font-mono text-muted-fg whitespace-nowrap">
                      {evt.caseNumber ? formatCNJ(evt.caseNumber) : '—'}
                    </td>
                    <td className="py-3 px-4 text-muted-fg font-mono whitespace-nowrap">
                      {evt.assignee || 'admin'}
                    </td>
                    <td className="py-3 px-4 text-right whitespace-nowrap">
                      <Button
                        variant="ghost"
                        size="xs"
                        onClick={(e) => {
                          e.stopPropagation();
                          if (evt.caseId && onSelectCase) onSelectCase(evt.caseId);
                        }}
                      >
                        Ver Autos
                      </Button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* Selected Event Details Modal / Drawer */}
      {selectedEvent && (
        <div className="fixed inset-0 z-50 overflow-y-auto flex items-center justify-center p-4">
          <div className="fixed inset-0 bg-black/80 transition-opacity" onClick={() => setSelectedEvent(null)} />
          <div className="relative w-full max-w-lg bg-surface border border-border rounded-none p-6 shadow-none z-10 text-xs space-y-4 animate-modal-pop">
            <div className="flex items-center justify-between border-b border-border pb-3">
              <Badge variant={selectedEvent.type === 'respite' ? 'red' : 'yellow'}>
                {selectedEvent.type === 'respite' ? 'Prazo Processual Fatal' : 'Audiência Judicial'}
              </Badge>
              <button
                onClick={() => setSelectedEvent(null)}
                className="text-muted-fg hover:text-fg font-mono text-sm cursor-pointer"
              >
                ✕
              </button>
            </div>

            <div>
              <h3 className="text-sm font-bold text-fg font-heading leading-snug">{selectedEvent.summary}</h3>
              <div className="mt-3 grid grid-cols-2 gap-2 text-[11px] font-mono">
                <div className="p-3 bg-bg border border-border">
                  <span className="text-muted-fg block text-[10px] uppercase">Data / Horário</span>
                  <span className="text-fg font-bold">
                    {new Date(selectedEvent.start).toLocaleString('pt-BR')}
                  </span>
                </div>
                <div className="p-3 bg-bg border border-border">
                  <span className="text-muted-fg block text-[10px] uppercase">Responsável</span>
                  <span className="text-fg font-bold">{selectedEvent.assignee || 'Gabinete'}</span>
                </div>
              </div>
            </div>

            {selectedEvent.caseNumber && (
              <div className="p-3 bg-bg border border-border flex items-center justify-between">
                <div>
                  <span className="text-muted-fg block text-[10px] uppercase font-mono">Processo Vinculado</span>
                  <span className="font-mono font-bold text-fg">{formatCNJ(selectedEvent.caseNumber)}</span>
                  <p className="text-[11px] text-muted-fg truncate mt-0.5">{selectedEvent.caseName}</p>
                </div>
                {onSelectCase && selectedEvent.caseId && (
                  <Button
                    variant="secondary"
                    size="xs"
                    onClick={() => {
                      onSelectCase(selectedEvent.caseId!);
                      setSelectedEvent(null);
                    }}
                  >
                    Abrir Autos
                  </Button>
                )}
              </div>
            )}

            {selectedEvent.location && (
              <div className="flex items-center gap-2 text-muted-fg">
                <MapPin className="h-3.5 w-3.5 text-accent shrink-0" />
                <span className="font-mono text-[11px]">{selectedEvent.location}</span>
              </div>
            )}

            <div className="flex justify-end gap-2 pt-3 border-t border-border">
              <Button variant="outline" size="sm" onClick={() => setSelectedEvent(null)}>
                Fechar
              </Button>
              <Button variant="primary" size="sm" onClick={() => setSelectedEvent(null)}>
                Marcar como Cumprido
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
