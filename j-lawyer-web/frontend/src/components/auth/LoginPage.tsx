import React, { useState } from 'react';
import { Scale, Lock, User, AlertCircle, ArrowRight, ShieldCheck } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../common/Button';
import { Input } from '../common/Input';

export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('a');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setError('Por favor, preencha o usuário e a senha.');
      return;
    }

    setError(null);
    setIsLoading(true);

    try {
      await login(username, password);
    } catch (err: any) {
      if (err.response?.status === 401) {
        setError('Credenciais inválidas. Verifique seu usuário e senha.');
      } else if (err.code === 'ERR_NETWORK' || !err.response) {
        setError('Não foi possível conectar ao servidor WildFly (http://localhost:8000). Verifique se o servidor está em execução.');
      } else {
        setError(err.response?.data?.error || 'Erro ao realizar login no servidor.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-screen bg-[#0A0A0A] flex flex-col items-center justify-center p-4 selection:bg-[#FF3D00] selection:text-[#0A0A0A] relative">
      <div className="relative w-full max-w-md bg-[#0F0F0F] border border-[#262626] rounded-none p-8 z-10">
        {/* Brand Header */}
        <div className="flex flex-col items-center text-center mb-8">
          <div className="h-12 w-12 bg-[#141414] border border-[#262626] flex items-center justify-center mb-4">
            <img src="/icons/atrium-emblem.svg" alt="BR-LAWYER" className="h-6 w-6" />
          </div>
          <h1 className="text-3xl font-black font-heading tracking-tighter text-[#FAFAFA]">
            BR-LAWYER
          </h1>
          <p className="text-xs text-[#737373] mt-1 font-mono uppercase tracking-widest">
            Prática Jurídica & Gestão Processual
          </p>
          <div className="mt-4 flex items-center gap-1.5 px-3 py-1 bg-[#141414] border border-[#262626] text-[10px] text-[#737373] font-mono uppercase tracking-wider">
            <ShieldCheck className="h-3.5 w-3.5 text-emerald-400" />
            <span>WildFly Elytron JWT</span>
          </div>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-3.5 bg-rose-950/30 border border-rose-600/40 flex items-start gap-2.5 text-xs text-rose-400">
            <AlertCircle className="h-4 w-4 shrink-0 mt-0.5 text-rose-500" />
            <div>{error}</div>
          </div>
        )}

        {/* Login Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-[11px] font-mono uppercase tracking-wider text-[#737373] mb-1.5">
              Usuário / Operador
            </label>
            <Input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="ex: admin"
              leftIcon={<User className="h-3.5 w-3.5" />}
              autoComplete="username"
              required
            />
          </div>

          <div>
            <label className="block text-[11px] font-mono uppercase tracking-wider text-[#737373] mb-1.5">
              Senha
            </label>
            <Input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              leftIcon={<Lock className="h-3.5 w-3.5" />}
              autoComplete="current-password"
              required
            />
          </div>

          <Button
            type="submit"
            variant="primary"
            size="md"
            isLoading={isLoading}
            className="w-full mt-6"
            rightIcon={<ArrowRight className="h-4 w-4" />}
          >
            Acessar Sistema
          </Button>
        </form>

        {/* Development Tip */}
        <div className="mt-8 pt-4 border-t border-[#262626] text-center font-mono">
          <p className="text-[11px] text-[#737373]">
            Ambiente Local: <span className="text-[#FAFAFA] font-bold">admin</span> / <span className="text-[#FAFAFA] font-bold">a</span>
          </p>
          <p className="text-[10px] text-[#525252] mt-1">
            Conectado a <span className="text-[#737373]">http://localhost:8000</span>
          </p>
        </div>
      </div>
    </div>
  );
};