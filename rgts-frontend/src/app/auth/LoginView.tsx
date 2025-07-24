import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/shared/utils';
import type { ComponentProps } from 'react';
import { Link } from 'react-router-dom';

export function LoginView({ className, ...props }: ComponentProps<'div'>) {
  return (
    <div className={cn('flex items-center justify-center min-h-screen bg-muted', className)} {...props}>
      <Card className='w-full max-w-md shadow-xl'>
        <CardHeader className='text-center'>
          <CardTitle className='text-2xl'>Login</CardTitle>
          <CardDescription>Enter your credentials to access your account</CardDescription>
        </CardHeader>
        <CardContent>
          <form className='space-y-6'>
            <div className='grid gap-4'>
              <div className='grid gap-2'>
                <Label htmlFor='email'>Email</Label>
                <Input id='email' type='email' required />
              </div>
              <div className='grid gap-2'>
                <div className='flex items-center justify-between'>
                  <Label htmlFor='password'>Password</Label>
                  <Link to='/forgot-password' className='text-sm text-muted-foreground hover:underline'>
                    Forgot password?
                  </Link>
                </div>
                <Input id='password' type='password' required />
              </div>
              <Button type='submit' className='w-full'>
                Sign In
              </Button>
            </div>
          </form>

          <div className='mt-6 text-center text-sm text-muted-foreground'>
            Don&apos;t have an account?{' '}
            <Link to='/register' className='font-medium text-primary hover:underline'>
              Sign up
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
