import { UsuarioCadastroData, UsuarioLoginData } from "@/src/types/authTypes";
import * as SecureStore from 'expo-secure-store';
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { usuarioCadastro, usuarioLogin } from "../api/usuarioApi";
import { UsuarioResponse } from "../types/usuarioTypes";

type AuthContextData = {
  signUp: (data: UsuarioCadastroData) => Promise<void>;
  signIn: (data: UsuarioLoginData) => Promise<void>;
  signOut: () => void;
  session?: string | null;
  isLoading: boolean;
  usuario: UsuarioResponse | null;
}

export const AuthContext = React.createContext<AuthContextData>({
  signUp: async () => {},
  signIn: async () => {},
  signOut: () => null,
  session: null,
  isLoading: true,
  usuario: null,
});

export function useSession() {
  const value = React.useContext(AuthContext);
  if (process.env.NODE_ENV !== "production") {
    if (!value) {
      throw new Error("useSession must be wrapped in a <AuthProvider />")
    }
  }
  return value;
}

export function AuthProvider(props: React.PropsWithChildren) {
  const [session, setSession] = useState<string | null>(null);
  const [usuario, setUsuario] = useState<UsuarioResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadStorageData() {
      try {
        setIsLoading(true);
        
        const storedToken = await SecureStore.getItemAsync('userToken');
        const storedUser = await SecureStore.getItemAsync('userData');

        if (storedToken && storedUser) {
          setSession(storedToken);
          setUsuario(JSON.parse(storedUser));
        }
      } catch (error) {
        console.error("Erro ao restaurar sessão:", error);
        await signOut(); 
      } finally {
        setIsLoading(false);
      }
    }

    loadStorageData();
  }, []);

  const signUp = useCallback(async (data: UsuarioCadastroData) =>{
    try {
      const response = await usuarioCadastro(data);
      
      if (response.token && response.usuario) {
        setSession(response.token);
        setUsuario(response.usuario);

        await SecureStore.setItemAsync('userToken', response.token);
        await SecureStore.setItemAsync('userData', JSON.stringify(response.usuario));
      }
    } catch (error) {
      console.error("Erro no signUp: ", error);
      throw error;
    }
  }, []);

  const signIn = useCallback(async (data: UsuarioLoginData) => {
    try {
      const response = await usuarioLogin(data);
      
      if (response.token && response.usuario) {
        setSession(response.token);
        setUsuario(response.usuario);

        await SecureStore.setItemAsync('userToken', response.token);
        await SecureStore.setItemAsync('userData', JSON.stringify(response.usuario));
      }
    } catch (error) {
      console.error("Erro no signIn: ", error);
      throw error;
    }
  }, []);

  const signOut = useCallback(async () => {
    setSession(null);
    setUsuario(null);
    
    await SecureStore.deleteItemAsync('userToken');
    await SecureStore.deleteItemAsync('userData');
  }, []);

  const providerValue = useMemo(() => ({
    signUp,
    signIn,
    signOut,
    session,
    isLoading,
    usuario
  }), [signUp, signIn, signOut, session, isLoading, usuario]);

  return (
    <AuthContext.Provider value={providerValue}>
      {props.children}
    </AuthContext.Provider>
  )
}
