import axios from 'axios';

const BASE_URL = process.env.EXPO_PUBLIC_API_URL || "http://academico3.rj.senac.br/vitta";

export const apiManager = axios.create({
  baseURL: BASE_URL,
  responseType: 'json',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiManager.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && (error.response.status === 403 || error.response.status === 401)) {
      console.error("Sessão expirada ou inválida. Deslogando...");
      
    }
    return Promise.reject(error);
  }
);

export default apiManager;