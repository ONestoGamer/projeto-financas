'use client';

import { useQuery } from '@tanstack/react-query';
import { transactionService } from '@/services/transactionService';
import DashboardLayout from '@/components/layout/DashboardLayout';
import { Transaction, TransactionType } from '@/types';
import { TrendingUp, TrendingDown, DollarSign, Activity } from 'lucide-react';
import { useMemo } from 'react';

export default function DashboardPage() {
  const { data: transactions = [], isLoading } = useQuery({
    queryKey: ['transactions'],
    queryFn: transactionService.getAll,
  });

  const stats = useMemo(() => {
    const totalIncome = transactions
      .filter((t) => t.type === TransactionType.INCOME)
      .reduce((sum, t) => sum + t.amount, 0);

    const totalExpense = transactions
      .filter((t) => t.type === TransactionType.EXPENSE)
      .reduce((sum, t) => sum + t.amount, 0);

    const balance = totalIncome - totalExpense;

    return {
      totalIncome,
      totalExpense,
      balance,
      transactionCount: transactions.length,
    };
  }, [transactions]);

  const recentTransactions = transactions. slice(0, 5);

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value);
  };

  if (isLoading) {
    return (
      <DashboardLayout>
        <div className="flex items-center justify-center h-96">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-1">Visão geral das suas finanças</p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {/* Balance */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Saldo Total</p>
                <p className="text-2xl font-bold text-gray-900 mt-2">
                  {formatCurrency(stats.balance)}
                </p>
              </div>
              <div className="bg-blue-100 p-3 rounded-full">
                <DollarSign className="w-6 h-6 text-blue-600" />
              </div>
            </div>
          </div>

          {/* Income */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Receitas</p>
                <p className="text-2xl font-bold text-green-600 mt-2">
                  {formatCurrency(stats.totalIncome)}
                </p>
              </div>
              <div className="bg-green-100 p-3 rounded-full">
                <TrendingUp className="w-6 h-6 text-green-600" />
              </div>
            </div>
          </div>

          {/* Expenses */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Despesas</p>
                <p className="text-2xl font-bold text-red-600 mt-2">
                  {formatCurrency(stats. totalExpense)}
                </p>
              </div>
              <div className="bg-red-100 p-3 rounded-full">
                <TrendingDown className="w-6 h-6 text-red-600" />
              </div>
            </div>
          </div>

          {/* Transactions */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Transações</p>
                <p className="text-2xl font-bold text-gray-900 mt-2">
                  {stats.transactionCount}
                </p>
              </div>
              <div className="bg-purple-100 p-3 rounded-full">
                <Activity className="w-6 h-6 text-purple-600" />
              </div>
            </div>
          </div>
        </div>

        {/* Recent Transactions */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100">
          <div className="p-6 border-b border-gray-100">
            <h2 className="text-xl font-bold text-gray-900">Transações Recentes</h2>
          </div>
          <div className="p-6">
            {recentTransactions.length === 0 ? (
              <p className="text-center text-gray-500 py-8">
                Nenhuma transação encontrada
              </p>
            ) : (
              <div className="space-y-4">
                {recentTransactions.map((transaction) => (
                  <div
                    key={transaction.id}
                    className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                  >
                    <div className="flex items-center gap-4">
                      <div
                        className={`p-2 rounded-full ${
                          transaction.type === TransactionType. INCOME
                            ? 'bg-green-100'
                            : 'bg-red-100'
                        }`}
                      >
                        {transaction.type === TransactionType.INCOME ? (
                          <TrendingUp className="w-5 h-5 text-green-600" />
                        ) : (
                          <TrendingDown className="w-5 h-5 text-red-600" />
                        )}
                      </div>
                      <div>
                        <p className="font-medium text-gray-900">
                          {transaction.description}
                        </p>
                        <p className="text-sm text-gray-500">
                          {transaction.category. name} •{' '}
                          {new Date(transaction.date).toLocaleDateString('pt-BR')}
                        </p>
                      </div>
                    </div>
                    <p
                      className={`text-lg font-semibold ${
                        transaction.type === TransactionType.INCOME
                          ? 'text-green-600'
                          : 'text-red-600'
                      }`}
                    >
                      {transaction.type === TransactionType. INCOME ? '+' : '-'}
                      {formatCurrency(transaction.amount)}
                    </p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}