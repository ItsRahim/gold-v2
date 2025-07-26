import { type ChangeEvent, type FormEvent, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/shared/utils';
import type { ComponentProps } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { showToast, TOAST_TYPES } from '@/components/shared/ToastNotification';
import { useAuthStore } from '@/stores/AuthStore';
import { DarkMode } from '@/shared/theme/DarkMode.tsx';

export function LoginView({ className, ...props }: ComponentProps<'div'>) {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setFormData((prev) => ({ ...prev, [id]: value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);

    const success = await login(formData);

    setLoading(false);

    if (success) {
      navigate('/dashboard');
    } else {
      showToast(TOAST_TYPES.ERROR, 'Invalid username or password.');
    }
  };

  return (
    <div className={cn('flex items-center justify-center min-h-screen bg-muted', className)} {...props}>
      <Card className='w-full max-w-md shadow-xl'>
        <CardHeader className='text-center'>
          <CardTitle className='text-2xl'>Login</CardTitle>
          <CardDescription>Enter your credentials to access your account</CardDescription>
        </CardHeader>
        <CardContent>
          <form className='space-y-6' onSubmit={handleSubmit}>
            <div className='grid gap-4'>
              <div className='grid gap-2'>
                <Label htmlFor='username'>Username</Label>
                <Input id='username' type='text' value={formData.username} onChange={handleChange} required />
              </div>
              <div className='grid gap-2'>
                <div className='flex items-center justify-between'>
                  <Label htmlFor='password'>Password</Label>
                  <Link to='/forgot-password' className='text-sm text-muted-foreground hover:underline'>
                    Forgot password?
                  </Link>
                </div>
                <Input id='password' type='password' value={formData.password} onChange={handleChange} required />
              </div>
              <Button type='submit' className='w-full' disabled={loading}>
                {loading ? 'Signing In...' : 'Sign In'}
              </Button>
            </div>
          </form>

          <div className='mt-6 text-center text-sm text-muted-foreground'>
            Don&apos;t have an account?{' '}
            <Link to='/register' className='font-medium text-primary hover:underline'>
              Sign up
            </Link>
          </div>
          <div className='absolute top-4 right-4'>
            <DarkMode />
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
