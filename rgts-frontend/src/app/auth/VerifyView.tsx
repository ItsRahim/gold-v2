import React, { useState, useRef, type FormEvent } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/shared/utils';
import type { ComponentProps } from 'react';
import { useNavigate } from 'react-router-dom';
import { showToast, TOAST_TYPES } from '@/components/shared/ToastNotification';
import { DarkMode } from '@/shared/theme/DarkMode.tsx';
import { useAuthStore } from '@/stores/AuthStore';
import { ArrowLeft } from 'lucide-react';

export function VerifyView({ className, ...props }: ComponentProps<'div'>) {
  const navigate = useNavigate();
  const verify = useAuthStore((state) => state.verify);
  const [loading, setLoading] = useState(false);

  const [email, setEmail] = useState('');
  const [codeDigits, setCodeDigits] = useState(['', '', '', '', '', '']);
  const inputRefs = useRef<Array<HTMLInputElement | null>>(Array(6).fill(null));

  const handleDigitChange = (index: number, value: string) => {
    const upperValue = value.toUpperCase();

    if (!/^[A-Z0-9]?$/.test(upperValue)) return;

    const newDigits = [...codeDigits];
    newDigits[index] = upperValue;
    setCodeDigits(newDigits);

    if (upperValue && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !codeDigits[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const verificationCode = codeDigits.join('');
    if (verificationCode.length !== 6) {
      showToast(TOAST_TYPES.ERROR, 'Please enter the 6-digit verification code.');
      return;
    }

    setLoading(true);
    const success = await verify({ email, verificationCode });
    console.log();
    setLoading(false);

    if (success) {
      navigate('/login');
    }
  };

  return (
    <div className={cn('relative flex items-center justify-center min-h-screen bg-muted', className)} {...props}>
      <div className='absolute top-4 right-4'>
        <DarkMode />
      </div>
      <Card className='w-full max-w-md shadow-xl'>
        <button type='button' onClick={() => navigate('/login')} className='absolute top-4 left-4 p-1 rounded hover:bg-accent' aria-label='Go back'>
          <ArrowLeft className='w-5 h-5' />
        </button>
        <CardHeader className='text-center'>
          <CardTitle className='text-2xl'>Verify Your Account</CardTitle>
          <CardDescription>Enter the 6-digit code sent to your email</CardDescription>
        </CardHeader>
        <CardContent>
          <form className='space-y-6' onSubmit={handleSubmit}>
            <div className='grid gap-4'>
              <div className='grid gap-2'>
                <Label htmlFor='email'>Email</Label>
                <Input id='email' type='email' value={email} onChange={(e) => setEmail(e.target.value)} required />
              </div>
              <div className='grid gap-2'>
                <Label>Verification Code</Label>
                <div className='flex gap-2 justify-center'>
                  {codeDigits.map((digit, index) => (
                    <Input
                      key={index}
                      ref={(el) => {
                        inputRefs.current[index] = el;
                      }}
                      type='text'
                      inputMode='text'
                      autoCapitalize='characters'
                      maxLength={1}
                      value={digit}
                      onChange={(e) => handleDigitChange(index, e.target.value)}
                      onKeyDown={(e) => handleKeyDown(index, e)}
                      className='text-center w-12 h-12 text-xl uppercase'
                      required
                    />
                  ))}
                </div>
              </div>
              <Button type='submit' className='w-full' disabled={loading}>
                {loading ? 'Verifying...' : 'Verify'}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
