import api from '@/lib/axios';
import { Transaction, TransactionRequest } from '@/types';

export const transactionService = {
  getAll: async (): Promise<Transaction[]> => {
    const response = await api. get('/transactions');
    return response.data;
  },

  getById: async (id: string): Promise<Transaction> => {
    const response = await api.get(`/transactions/${id}`);
    return response.data;
  },

  create:  async (data: TransactionRequest): Promise<Transaction> => {
    const response = await api.post('/transactions', data);
    return response.data;
  },

  update: async (id: string, data: TransactionRequest): Promise<Transaction> => {
    const response = await api.put(`/transactions/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/transactions/${id}`);
  },
};