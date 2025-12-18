export interface User {
  id: string;
  name: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE',
}

export interface Category {
  id: string;
  name: string;
  icon?:  string;
  color?: string;
  type: TransactionType;
}

export interface Transaction {
  id: string;
  type: TransactionType;
  amount: number;
  description: string;
  date: string;
  attachment?: string;
  category: Category;
  createdAt: string;
  updatedAt: string;
}

export interface TransactionRequest {
  type: TransactionType;
  amount: number;
  description: string;
  date:  string;
  categoryId: string;
  attachment?:  string;
}

export interface DashboardStats {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  transactionCount: number;
}