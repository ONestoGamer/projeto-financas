'use client';

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { transactionService } from '@/services/transactionService';
import { Transaction, TransactionType, TransactionRequest } from '@/types';
import { X } from 'lucide-react';

const transactionSchema = z.object({
  type: z.nativeEnum(TransactionType),
  amount: z.number().positive('Valor deve ser positivo'),
  description: z.string().min(3, 'Descrição deve ter no mínimo 3 caracteres'),
  date: z.string(),
  categoryId: z.string().min(1, 'Categoria é obrigatória'),
});

type TransactionForm = z.infer<typeof transactionSchema>;

interface TransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
  transaction?:  Transaction | null;
}

export default function TransactionModal({
  isOpen,
  onClose,
  transaction,
}:  TransactionModalProps) {
  const queryClient = useQueryClient();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TransactionForm>({
    resolver: zodResolver(transactionSchema),
    defaultValues: {
      type:  TransactionType.EXPENSE,
      date: new Date().toISOString().split('T')[0],
    },
  });

  useEffect(() => {
    if (transaction) {
      reset({
        type: transaction.type,
        amount: transaction.amount,
        description: transaction.description,
        date: transaction.date,
        categoryId: transaction.category.id,
      });
    } else {
      reset({
        type: TransactionType.EXPENSE,
        date: new Date().toISOString().split('T')[0],
      });
    }
  }, [transaction, reset]);

  const createMutation = useMutation({
    mutationFn: transactionService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transactions'] });
      onClose();
      reset();
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: TransactionRequest }) =>
      transactionService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transactions'] });
      onClose();
      reset();
    },
  });

  const onSubmit = async (data: TransactionForm) => {
    const requestData:  TransactionRequest = {
      type: data.type,
      amount: data.amount,
      description: data.description,
      date: data.date,
      categoryId: data.categoryId,
    };

    if (transaction) {
      await updateMutation.mutateAsync({ id: transaction.id, data: requestData });
    } else {
      await createMutation.mutateAsync(requestData);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-2xl font-bold text-gray-900">
            {transaction ? 'Editar Transação' : 'Nova Transação'}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4">
          {/* Type */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Tipo
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label className="relative flex items-center justify-center cursor-pointer">
                <input
                  {... register('type')}
                  type="radio"
                  value={TransactionType.INCOME}
                  className="sr-only peer"
                />
                <div className="w-full px-4 py-3 text-center border-2 border-gray-300 rounded-lg peer-checked:border-green-500 peer-checked:bg-green-50 peer-checked:text-green-700 transition-all">
                  Receita
                </div>
              </label>
              <label className="relative flex items-center justify-center cursor-pointer">
                <input
                  {...register('type')}
                  type="radio"
                  value={TransactionType.EXPENSE}
                  className="sr-only peer"
                />
                <div className="w-full px-4 py-3 text-center border-2 border-gray-300 rounded-lg peer-checked:border-red-500 peer-checked:bg-red-50 peer-checked:text-red-700 transition-all">
                  Despesa
                </div>
              </label>
            </div>
          </div>

          {/* Amount */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Valor
            </label>
            <input
              {... register('amount', { valueAsNumber: true })}
              type="number"
              step="0.01"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              placeholder="0,00"
            />
            {errors.amount && (
              <p className="mt-1 text-sm text-red-600">{errors.amount.message}</p>
            )}
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descrição
            </label>
            <input
              {...register('description')}
              type="text"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              placeholder="Ex: Salário, Aluguel, Compras..."
            />
            {errors. description && (
              <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>
            )}
          </div>

          {/* Date */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Data
            </label>
            <input
              {...register('date')}
              type="date"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            />
            {errors.date && (
              <p className="mt-1 text-sm text-red-600">{errors.date.message}</p>
            )}
          </div>

          {/* Category - Placeholder (você precisará criar o serviço de categorias) */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Categoria
            </label>
            <select
              {...register('categoryId')}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            >
              <option value="">Selecione uma categoria</option>
              {/* Aqui você precisará buscar as categorias do backend */}
            </select>
            {errors. categoryId && (
              <p className="mt-1 text-sm text-red-600">{errors.categoryId.message}</p>
            )}
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={createMutation.isPending || updateMutation.isPending}
              className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors disabled:opacity-50"
            >
              {createMutation.isPending || updateMutation.isPending
                ? 'Salvando...'
                : transaction
                ? 'Atualizar'
                : 'Criar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}