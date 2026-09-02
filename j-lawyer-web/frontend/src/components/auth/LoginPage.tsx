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
    <div className="min-h-screen w-screen bg-[#030304] bg-grid-pattern flex flex-col items-center justify-center p-4 selection:bg-[#F7931A] selection:text-white relative overflow-hidden">
      {/* Background Ambient Heat Glow */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[350px] bg-[#F7931A]/10 blur-[140px] pointer-events-none rounded-full" />

      <div className="relative w-full max-w-md bg-[#0F1115]/90 border border-white/10 rounded-2xl shadow-[0_0_50px_-10px_rgba(247,147,26,0.15)] p-8 backdrop-blur-xl z-10">
        {/* Brand Header */}
        <div className="flex flex-col items-center text-center mb-8">
          <div className="h-14 w-14 rounded-2xl bg-[#EA580C]/15 border border-[#F7931A]/40 flex items-center justify-center mb-4 shadow-[0_0_20px_rgba(247,147,26,0.3)] animate-float">
            <img src="/icons/atrium-emblem.svg" alt="BR-LAWYER" className="h-8 w-8" />
          </div>
          <h1 className="text-2xl font-bold font-serif tracking-tight text-transparent bg-clip-text bg-gradient-to-r from-[#F7931A] to-[#FFD600]">
            BR-LAWYER
          </h1>
          <p className="text-xs text-slate-400 mt-1 font-sans">
            Plataforma de Prática Jurídica & Gestão Processual
          </p>
          <div className="mt-3 flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#181B20] border border-white/10 text-[11px] text-slate-300 font-mono shadow-xs">
            <ShieldCheck className="h-3.5 w-3.5 text-emerald-400" />
            <span>WildFly Elytron JWT Authentication</span>
          </div>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 flex items-start gap-2.5 text-xs text-rose-300 shadow-sm">
            <AlertCircle className="h-4 w-4 shrink-0 mt-0.5 text-rose-400" />
            <div>{error}</div>
          </div>
        )}

        {/* Login Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1.5">
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
            <label className="block text-xs font-medium text-slate-300 mb-1.5">
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
        <div className="mt-8 pt-4 border-t border-white/10 text-center">
          <p className="text-[11px] text-slate-400">
            Ambiente Local: <span className="font-mono text-[#FFD600]">admin</span> / <span className="font-mono text-[#FFD600]">a</span>
          </p>
          <p className="text-[10px] text-slate-500 mt-1">
            Conectando diretamente a <span className="font-mono text-slate-400">http://localhost:8000</span>
          </p>
        </div>
      </div>
    </div>
  );
};