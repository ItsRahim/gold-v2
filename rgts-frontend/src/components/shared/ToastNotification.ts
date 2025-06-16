import { createElement } from 'react';
import { toast } from 'sonner';
import { AlertCircle, CheckCircle, Info } from 'lucide-react';

export const TOAST_TYPES = {
  SUCCESS: 'success',
  ERROR: 'error',
  WARNING: 'warning',
} as const;

type ToastType = (typeof TOAST_TYPES)[keyof typeof TOAST_TYPES];

const iconMap = {
  [TOAST_TYPES.SUCCESS]: CheckCircle,
  [TOAST_TYPES.ERROR]: AlertCircle,
  [TOAST_TYPES.WARNING]: Info,
};

export const showToast = (type: ToastType, message: string): void => {
  const Icon = iconMap[type];

  if (type === TOAST_TYPES.SUCCESS) {
    toast.success(message, {
      icon: createElement(Icon, { className: 'text-green-500' }),
    });
  } else if (type === TOAST_TYPES.ERROR) {
    toast.error(message, {
      icon: createElement(Icon, { className: 'text-red-500' }),
    });
  } else {
    toast(message, {
      icon: createElement(Icon, { className: 'text-yellow-500' }),
    });
  }
};
