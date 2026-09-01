import React, { createContext, useContext, useEffect, useState } from 'react';
import { authService } from '../api/authService';
import { UserSession } from '../types/auth';

interface AuthContextType {
  session: UserSession | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  hasRole: (role: string) => boolean;
  isAdmin: boolean;
  canWrite: boolean;
  canCreate: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [session, setSession] = useState<UserSession | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Silent session restore via refresh cookie on initial mount
  useEffect(() => {
    const restoreSession = async () => {
      try {
        const tokenData = await authService.refresh();
        setSession({
          principal: tokenData.username || tokenData.principal || 'admin',
          roles: tokenData.roles || [],
          accessToken: tokenData.accessToken,
          expiresAt: Date.now() + tokenData.expiresIn * 1000,
        });
      } catch (err) {
        // No active refresh session
        setSession(null);
      } finally {
        setIsLoading(false);
      }
    };

    const handleExpired = () => {
      setSession(null);
    };

    window.addEventListener('jlawyer:auth_expired', handleExpired);
    restoreSession();

    return () => {
      window.removeEventListener('jlawyer:auth_expired', handleExpired);
    };
  }, []);

  const login = async (username: string, password: string) => {
    setIsLoading(true);
    try {
      const tokenData = await authService.login({ username, password });
      setSession({
        principal: tokenData.username || tokenData.principal || username,
        roles: tokenData.roles || [],
        accessToken: tokenData.accessToken,
        expiresAt: Date.now() + tokenData.expiresIn * 1000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } finally {
      setSession(null);
    }
  };

  const hasRole = (role: string) => {
    if (!session) return false;
    return session.roles.includes(role);
  };

  const isAdmin = hasRole('adminRole');
  const canWrite = hasRole('writeArchiveFileRole') || isAdmin;
  const canCreate = hasRole('createArchiveFileRole') || isAdmin;

  return (
    <AuthContext.Provider
      value={{
        session,
        isAuthenticated: !!session,
        isLoading,
        login,
        logout,
        hasRole,
        isAdmin,
        canWrite,
        canCreate,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};