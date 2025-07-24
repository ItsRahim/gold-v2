import { type ChangeEvent, type FormEvent, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/shared/utils';
import type { ComponentProps } from 'react';
import { Link } from 'react-router-dom';
import { showToast, TOAST_TYPES } from '@/components/shared/ToastNotification.ts';

export function RegisterView({ className, ...props }: ComponentProps<'div'>) {
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

    setFormData({ ...formData, [id]: updatedValue });
  };

  const isValidPhone = (phone: string) => /^[\d\s-]+$/.test(phone);

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();

    if (!isValidPhone(formData.phoneNumber)) {
      showToast(TOAST_TYPES.ERROR, 'Phone number can only contain numbers, spaces, or dashes.');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      showToast(TOAST_TYPES.ERROR, 'Passwords do not match.');
      return;
    }

    showToast(TOAST_TYPES.SUCCESS, 'Successfully registered!');
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
              <div className='grid gap-2'>
                <Label htmlFor='firstName'>First Name</Label>
                <Input id='firstName' value={formData.firstName} onChange={handleChange} required />
              </div>
              <div className='grid gap-2'>
                <Label htmlFor='lastName'>Last Name</Label>
                <Input id='lastName' value={formData.lastName} onChange={handleChange} required />
              </div>
              <div className='grid gap-2'>
                <Label htmlFor='email'>Email</Label>
                <Input id='email' type='email' value={formData.email} onChange={handleChange} required />
              </div>
              <div className='grid gap-2'>
                <Label htmlFor='username'>Username</Label>
                <Input id='username' value={formData.username} onChange={handleChange} required />
              </div>
              <div className='grid gap-2'>
                <Label htmlFor='phoneNumber'>Phone Number</Label>
                <Input id='phoneNumber' type='tel' value={formData.phoneNumber} onChange={handleChange} required />
              </div>
              <div className='grid gap-2'>
                <Label htmlFor='password'>Password</Label>
                <Input id='password' type='password' value={formData.password} onChange={handleChange} required />
              </div>
              <div className='grid gap-2'>
                <Label htmlFor='confirmPassword'>Confirm Password</Label>
                <Input id='confirmPassword' type='password' value={formData.confirmPassword} onChange={handleChange} required />
              </div>
              <Button type='submit' className='w-full'>
                Register
              </Button>
            </div>
          </form>

          <div className='mt-6 text-center text-sm text-muted-foreground'>
            Already have an account?{' '}
            <Link to='/login' className='font-medium text-primary hover:underline'>
              Log in
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
