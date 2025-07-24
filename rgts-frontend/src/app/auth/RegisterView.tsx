import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/shared/utils';
import type { ComponentProps } from 'react';
import { Link } from 'react-router-dom';

export function RegisterView({ className, ...props }: ComponentProps<'div'>) {
  return (
    <div className={cn('flex items-center justify-center min-h-screen bg-muted', className)} {...props}>
      <Card className='w-full max-w-md shadow-xl'>
        <CardHeader className='text-center'>
          <CardTitle className='text-2xl'>Create an Account</CardTitle>
          <CardDescription>Fill in the details to get started</CardDescription>
        </CardHeader>
        <CardContent>
          <form className='space-y-6'>
            <div className='grid gap-4'>
              <div className='grid gap-2'>
                <Label htmlFor='name'>Full Name</Label>
                <Input id='name' type='text' placeholder='John Doe' required />
              </div>

              <div className='grid gap-2'>
                <Label htmlFor='email'>Email</Label>
                <Input id='email' type='email' placeholder='you@example.com' required />
              </div>

              <div className='grid gap-2'>
                <Label htmlFor='password'>Password</Label>
                <Input id='password' type='password' required />
              </div>

              {/* Optional field: Enable if needed */}
              {/* <div className="grid gap-2">
                <Label htmlFor="confirm-password">Confirm Password</Label>
                <Input id="confirm-password" type="password" required />
              </div> */}

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
