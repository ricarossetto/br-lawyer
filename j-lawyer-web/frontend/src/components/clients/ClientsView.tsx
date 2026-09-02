import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Users,
  Search,
  Plus,
  Building2,
  User,
  ShieldAlert,
  MapPin,
  Phone,
  Mail,
  Briefcase,
  ExternalLink,
  ChevronLeft,
  ChevronRight,
  Filter,
} from 'lucide-react';
import { clientsService } from '../../api/clientsService';
import { RestfulClientContact, ClientContactCreateUpdateRequest } from '../../types/clients';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { Input } from '../common/Input';
import { cn } from '../../utils/cn';
import { formatCNJ, formatBRL } from '../../utils/formatters';
import { ClientInspectorDrawer } from './ClientInspectorDrawer';
import { ClientCreateEditModal } from './ClientCreateEditModal';

interface ClientsViewProps {
  onSelectCase?: (caseId: string) => void;
}

export const ClientsView: React.FC<ClientsViewProps> = ({ onSelectCase }) => {
  const queryClient = useQueryClient();
  const [searchQuery, setSearchQuery] = useState('');
  const [roleFilter, setRoleFilter] = useState<string>('all');
  const [selectedContact, setSelectedContact] = useState<RestfulClientContact | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingContact, setEditingContact] = useState<RestfulClientContact | null>(null);

  const { data: contacts = [], isLoading } = useQuery({
    queryKey: ['clients-contacts', searchQuery, roleFilter],
    queryFn: () => clientsService.list({ query: searchQuery, role: roleFilter === 'all' ? undefined : roleFilter }),
  });

  const handleSaveContact = async (req: ClientContactCreateUpdateRequest) => {
    if (req.id) {
      await clientsService.update(req.id, req);
    } else {
      await clientsService.create(req);
    }
    queryClient.invalidateQueries({ queryKey: ['clients-contacts'] });
    setIsModalOpen(false);
    setEditingContact(null);
  };

  return (
    <div className="space-y-4 font-sans">
      {/* Control Ribbon */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3 bg-surface border border-border p-5 rounded-none">
        {/* Role Tabs */}
        <div className="flex items-center gap-1.5 bg-bg p-1 border border-border overflow-x-auto">
          {[
            { id: 'all', label: 'Todos os Contatos' },
            { id: 'CLIENT', label: 'Clientes' },
            { id: 'OPPOSING_PARTY', label: 'Partes Adversas' },
            { id: 'LAWYER', label: 'Advogados' },
            { id: 'AUTHORITY', label: 'Órgãos / Tribunais' },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setRoleFilter(tab.id)}
              className={cn(
                'px-3 py-1.5 text-[10px] font-mono uppercase tracking-wider rounded-none transition-colors cursor-pointer whitespace-nowrap',
                roleFilter === tab.id
                  ? 'bg-elevated text-fg border border-border font-bold'
                  : 'text-muted-fg hover:text-fg'
              )}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Search & Actions */}
        <div className="flex items-center gap-2.5">
          <Input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Buscar por nome, CPF/CNPJ, e-mail..."
            leftIcon={<Search className="h-3.5 w-3.5" />}
            className="w-72"
          />
          <Button
            variant="primary"
            size="sm"
            leftIcon={<Plus className="h-3.5 w-3.5" />}
            onClick={() => {
              setEditingContact(null);
              setIsModalOpen(true);
            }}
          >
            Novo Contato
          </Button>
        </div>
      </div>

      {/* Contacts Table */}
      <div className="bg-surface border border-border rounded-none overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="border-b border-border text-[10px] font-bold text-muted-fg uppercase tracking-wider bg-bg font-mono select-none">
                <th className="py-3 px-4">Nome / Razão Social</th>
                <th className="py-3 px-4">Papel no Sistema</th>
                <th className="py-3 px-4">Documento (CPF/CNPJ)</th>
                <th className="py-3 px-4">Localidade</th>
                <th className="py-3 px-4">Processos</th>
                <th className="py-3 px-4">Enriquecimento</th>
                <th className="py-3 px-4 text-right">Ação</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {isLoading ? (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-muted-fg font-mono text-xs">
                    Carregando contatos e clientes da base...
                  </td>
                </tr>
              ) : contacts.length === 0 ? (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-muted-fg font-mono text-xs">
                    Nenhum contato encontrado com os critérios informados.
                  </td>
                </tr>
              ) : (
                contacts.map((contact) => (
                  <tr
                    key={contact.id}
                    onClick={() => setSelectedContact(contact)}
                    className="hover:bg-elevated transition-colors cursor-pointer group"
                  >
                    <td className="py-3 px-4 font-bold text-fg group-hover:text-accent flex items-center gap-2 max-w-sm truncate">
                      {contact.type === 'COMPANY' || contact.type === 'AUTHORITY' ? (
                        <Building2 className="h-4 w-4 text-muted-fg shrink-0" />
                      ) : (
                        <User className="h-4 w-4 text-muted-fg shrink-0" />
                      )}
                      <span className="truncate">{contact.name}</span>
                    </td>
                    <td className="py-3 px-4 whitespace-nowrap">
                      <Badge
                        variant={
                          contact.role === 'CLIENT'
                            ? 'green'
                            : contact.role === 'OPPOSING_PARTY'
                            ? 'red'
                            : contact.role === 'LAWYER'
                            ? 'active'
                            : 'neutral'
                        }
                        size="sm"
                      >
                        {contact.role === 'CLIENT'
                          ? 'Cliente'
                          : contact.role === 'OPPOSING_PARTY'
                          ? 'Parte Adversa'
                          : contact.role === 'LAWYER'
                          ? 'Advogado'
                          : 'Interessado'}
                      </Badge>
                    </td>
                    <td className="py-3 px-4 font-mono font-bold text-fg whitespace-nowrap">
                      {contact.documentNumber || '—'}
                    </td>
                    <td className="py-3 px-4 text-muted-fg font-mono whitespace-nowrap text-[11px]">
                      {contact.city && contact.state ? `${contact.city}/${contact.state}` : '—'}
                    </td>
                    <td className="py-3 px-4 font-mono font-bold text-fg whitespace-nowrap">
                      {contact.casesCount ?? 0}
                    </td>
                    <td className="py-3 px-4 whitespace-nowrap">
                      {contact.enrichment ? (
                        <span className="px-2 py-0.5 text-[9px] font-mono font-bold uppercase bg-emerald-950/20 text-emerald-400 border border-emerald-800/40">
                          {contact.enrichment.provider} OK
                        </span>
                      ) : (
                        <span className="text-muted-fg font-mono text-[10px]">Pendente</span>
                      )}
                    </td>
                    <td className="py-3 px-4 text-right whitespace-nowrap">
                      <Button
                        variant="ghost"
                        size="xs"
                        onClick={(e) => {
                          e.stopPropagation();
                          setSelectedContact(contact);
                        }}
                      >
                        Inspecionar
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Footer */}
        <div className="px-6 py-3 border-t border-border bg-bg flex items-center justify-between text-xs text-muted-fg font-mono">
          <span>
            Total de <span className="font-bold text-fg">{contacts.length}</span> contatos cadastrados
          </span>
          <span className="text-[10px] text-muted-fg">Single Source of Truth • Local-First</span>
        </div>
      </div>

      {/* Inspector Drawer */}
      <ClientInspectorDrawer
        contact={selectedContact}
        onClose={() => setSelectedContact(null)}
        onEdit={(c) => {
          setEditingContact(c);
          setIsModalOpen(true);
        }}
        onSelectCase={onSelectCase}
      />

      {/* Create / Edit Modal with BrasilAPI / ViaCEP Enrichment */}
      <ClientCreateEditModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingContact(null);
        }}
        contact={editingContact}
        onSave={handleSaveContact}
      />
    </div>
  );
};
