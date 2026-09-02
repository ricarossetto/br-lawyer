import React from 'react';
import {
  User,
  Building2,
  Mail,
  Phone,
  MapPin,
  Briefcase,
  ShieldAlert,
  ExternalLink,
  Edit,
  DollarSign,
  FileText,
} from 'lucide-react';
import { Drawer } from '../common/Drawer';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { RestfulClientContact } from '../../types/clients';
import { formatBRL, formatCNJ } from '../../utils/formatters';

interface ClientInspectorDrawerProps {
  contact: RestfulClientContact | null;
  onClose: () => void;
  onEdit: (contact: RestfulClientContact) => void;
  onSelectCase?: (caseId: string) => void;
}

export const ClientInspectorDrawer: React.FC<ClientInspectorDrawerProps> = ({
  contact,
  onClose,
  onEdit,
  onSelectCase,
}) => {
  if (!contact) return null;

  return (
    <Drawer
      isOpen={!!contact}
      onClose={onClose}
      title={contact.name}
      subtitle={`Documento: ${contact.documentNumber || 'Não informado'}`}
      width="lg"
    >
      <div className="space-y-5 text-xs font-sans">
        {/* Action Header */}
        <div className="flex items-center justify-between p-4 bg-bg border border-border">
          <div className="flex items-center gap-2">
            <Badge
              variant={
                contact.role === 'CLIENT'
                  ? 'green'
                  : contact.role === 'OPPOSING_PARTY'
                  ? 'red'
                  : 'active'
              }
            >
              {contact.role === 'CLIENT'
                ? 'Cliente'
                : contact.role === 'OPPOSING_PARTY'
                ? 'Parte Adversa'
                : contact.role === 'LAWYER'
                ? 'Advogado'
                : 'Interessado'}
            </Badge>
            <Badge variant="mono">{contact.type === 'COMPANY' ? 'Pessoa Jurídica' : 'Pessoa Física'}</Badge>
          </div>
          <Button
            variant="secondary"
            size="xs"
            leftIcon={<Edit className="h-3.5 w-3.5" />}
            onClick={() => {
              onEdit(contact);
              onClose();
            }}
          >
            Editar Cadastro
          </Button>
        </div>

        {/* Conflict of Interest Warning if detected */}
        {contact.enrichment?.conflictHints && contact.enrichment.conflictHints.length > 0 && (
          <div className="p-3 bg-amber-950/20 border border-amber-800/50 text-amber-200 text-xs space-y-1">
            <div className="flex items-center gap-1.5 font-bold font-mono text-[10px] uppercase text-amber-400">
              <ShieldAlert className="h-4 w-4 text-amber-400" />
              <span>Possível Conflito de Interesses (Supervisão Obrigatória)</span>
            </div>
            {contact.enrichment.conflictHints.map((hint, idx) => (
              <p key={idx} className="text-[11px] font-sans text-amber-300/90">{hint}</p>
            ))}
          </div>
        )}

        {/* Contact Info Grid */}
        <div className="grid grid-cols-2 gap-3">
          <div className="p-3 bg-bg border border-border">
            <span className="text-muted-fg block text-[10px] uppercase font-mono tracking-wider font-bold">E-mail</span>
            <div className="flex items-center gap-1.5 mt-1 text-fg font-mono truncate">
              <Mail className="h-3.5 w-3.5 text-muted-fg shrink-0" />
              <span className="truncate">{contact.email || '—'}</span>
            </div>
          </div>
          <div className="p-3 bg-bg border border-border">
            <span className="text-muted-fg block text-[10px] uppercase font-mono tracking-wider font-bold">Telefone / WhatsApp</span>
            <div className="flex items-center gap-1.5 mt-1 text-fg font-mono">
              <Phone className="h-3.5 w-3.5 text-muted-fg shrink-0" />
              <span>{contact.cellphone || contact.phone || '—'}</span>
            </div>
          </div>
        </div>

        {/* Address */}
        <div className="p-3 bg-bg border border-border">
          <span className="text-muted-fg block text-[10px] uppercase font-mono tracking-wider font-bold mb-1">Endereço Cadastral</span>
          <div className="flex items-start gap-2 text-fg">
            <MapPin className="h-3.5 w-3.5 text-accent mt-0.5 shrink-0" />
            <span className="font-mono text-[11px]">
              {contact.street ? `${contact.street}, ${contact.number || 'S/N'} - ${contact.neighborhood || ''}, ${contact.city || ''}/${contact.state || ''} - CEP ${contact.zipCode || ''}` : 'Endereço não informado.'}
            </span>
          </div>
        </div>

        {/* QSA (Quadro de Sócios) if Company */}
        {contact.enrichment?.qsa && contact.enrichment.qsa.length > 0 && (
          <div className="space-y-2">
            <span className="text-fg font-bold font-mono text-[10px] uppercase tracking-wider block">
              Quadro de Sócios e Administradores (QSA - BrasilAPI)
            </span>
            <div className="space-y-1.5">
              {contact.enrichment.qsa.map((s, idx) => (
                <div key={idx} className="p-2.5 bg-bg border border-border flex items-center justify-between">
                  <span className="font-bold text-fg">{s.name}</span>
                  <Badge variant="mono" size="sm">{s.role}</Badge>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Linked Cases */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-fg font-bold font-mono text-[10px] uppercase tracking-wider">
              Processos Vinculados ({contact.casesCount ?? 0})
            </span>
            {contact.totalClaimValue && (
              <span className="text-accent font-mono font-bold text-xs">
                Total: {formatBRL(contact.totalClaimValue)}
              </span>
            )}
          </div>

          <div className="p-3 bg-bg border border-border flex items-center justify-between">
            <div>
              <span className="font-mono font-bold text-fg block">5001234-56.2026.4.04.7105</span>
              <span className="text-[11px] text-muted-fg">EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL</span>
            </div>
            {onSelectCase && (
              <Button
                variant="secondary"
                size="xs"
                rightIcon={<ExternalLink className="h-3.5 w-3.5" />}
                onClick={() => {
                  onSelectCase('5001234-56.2026.4.04.7105');
                  onClose();
                }}
              >
                Abrir Autos
              </Button>
            )}
          </div>
        </div>
      </div>
    </Drawer>
  );
};
