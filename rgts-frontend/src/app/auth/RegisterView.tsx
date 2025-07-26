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

export function RegisterView({ className, ...props }: ComponentProps<'div'>) {
  const navigate = useNavigate();
  const { register } = useAuthStore();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    username: '',
    phoneNumber: '',
    password: '',
    confirmPassword: '',
  });

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    let updatedValue = value;

    if (id === 'firstName' || id === 'lastName') {
      updatedValue = value.charAt(0).toUpperCase() + value.slice(1);
    }

    setFormData((prev) => ({ ...prev, [id]: updatedValue }));
  };

  const isValidPhone = (phone: string) => /^[\d\s-]+$/.test(phone);

  const getInputType = (field: string): string => {
    const typeMap: Record<string, string> = {
      email: 'email',
      phoneNumber: 'tel',
      password: 'password',
      confirmPassword: 'password',
    };
    return typeMap[field] || 'text';
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!isValidPhone(formData.phoneNumber)) {
      showToast(TOAST_TYPES.ERROR, 'Phone number can only contain numbers, spaces, or dashes.');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      showToast(TOAST_TYPES.ERROR, 'Passwords do not match.');
      return;
    }

    setLoading(true);
    const { confirmPassword, ...requestData } = formData;

    const success = await register(requestData);

    setLoading(false);

    if (success) {
      navigate('/verify');
    }
  };

  return (
    <div className={cn('flex items-center justify-center min-h-screen bg-muted', className)} {...props}>
      <Card className='w-full max-w-md shadow-xl'>
        <CardHeader className='text-center'>
          <CardTitle className='text-2xl'>Create an Account</CardTitle>
          <CardDescription>Fill in the details to get started</CardDescription>
        </CardHeader>
        <CardContent>
          <form className='space-y-6' onSubmit={handleSubmit}>
            <div className='grid gap-4'>
              {['firstName', 'lastName', 'email', 'username', 'phoneNumber', 'password', 'confirmPassword'].map((field) => (
                <div key={field} className='grid gap-2'>
                  <Label htmlFor={field}>
                    {
                      {
                        firstName: 'First Name',
                        lastName: 'Last Name',
                        email: 'Email',
                        username: 'Username',
                        phoneNumber: 'Phone Number',
                        password: 'Password',
                        confirmPassword: 'Confirm Password',
                      }[field]
                    }
                  </Label>
                  <Input id={field} type={getInputType(field)} value={formData[field as keyof typeof formData]} onChange={handleChange} required />
                </div>
              ))}
              <Button type='submit' className='w-full' disabled={loading}>
                {loading ? 'Registering...' : 'Register'}
              </Button>
            </div>
          </form>

          <div className='mt-6 text-center text-sm text-muted-foreground'>
            Already have an account?{' '}
            <Link to='/login' className='font-medium text-primary hover:underline'>
              Log in
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
