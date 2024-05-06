import * as React from "react";

import { cn } from "@/lib/utils";

interface TypographyProps {
    children: React.ReactNode;
    className?: string;
    [x: string]: any;
};

const H1 = React.forwardRef<HTMLHeadingElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <h1
      className={cn(
        "scroll-m-20 text-4xl font-extrabold tracking-tight lg:text-5xl",
        className
      )}
      ref={ref}
      {...props}
    >
      {children}
    </h1>
  );
});
H1.displayName = "H1";

const H2 = React.forwardRef<HTMLHeadingElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <h2
      className={cn(
        "scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight first:mt-0",
        className
      )}
      ref={ref}
      {...props}
    >
      {children}
    </h2>
  );
});
H2.displayName = "H2";

const H3 = React.forwardRef<HTMLHeadingElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <h3
      className={cn(
        "scroll-m-20 text-2xl font-semibold tracking-tight",
        className
      )}
      ref={ref}
      {...props}
    >
      {children}
    </h3>
  );
});
H3.displayName = "H3";

const H4 = React.forwardRef<HTMLHeadingElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <h4
      className={cn(
        "scroll-m-20 text-xl font-semibold tracking-tight",
        className
      )}
      ref={ref}
      {...props}
    >
      {children}
    </h4>
  );
});
H4.displayName = "H4";

const P = React.forwardRef<HTMLParagraphElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <p
      className={cn("leading-7 [&:not(:first-child)]:mt-6", className)}
      ref={ref}
      {...props}
    >
      {children}
    </p>
  );
});
P.displayName = "P";

const Blockquote = React.forwardRef<HTMLQuoteElement, TypographyProps>(
    ({ children, className, ...props }, ref) => {
    return (
      <blockquote
        className={cn("mt-6 border-l-2 pl-6 italic", className)}
        ref={ref}
        {...props}
      >
        {children}
      </blockquote>
    );
  }
);
Blockquote.displayName = "Blockquote";

const UL = React.forwardRef<HTMLUListElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <ul
      className={cn("my-6 ml-6 list-disc [&>li]:mt-2", className)}
      ref={ref}
      {...props}
    >
      {children}
    </ul>
  );
});
UL.displayName = "UL";

const Lead = React.forwardRef<HTMLParagraphElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <p
      className={cn("text-xl text-muted-foreground", className)}
      ref={ref}
      {...props}
    >
      {children}
    </p>
  );
});
Lead.displayName = "Lead";

const Large = React.forwardRef<HTMLParagraphElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <p
      className={cn("text-lg font-semibold", className)}
      ref={ref}
      {...props}
    >
      {children}
    </p>
  );
});
Large.displayName = "Large";

const Small = React.forwardRef<HTMLParagraphElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <p
      className={cn("text-sm font-medium leading-none", className)}
      ref={ref}
      {...props}
    >
      {children}
    </p>
  );
});
Small.displayName = "Small";

const Muted = React.forwardRef<HTMLParagraphElement, TypographyProps>(({ children, className, ...props }, ref) => {
  return (
    <p
      className={cn("text-sm text-muted-foreground", className)}
      ref={ref}
      {...props}
    >
      {children}
    </p>
  );
});
Muted.displayName = "Muted";

export { H1, H2, H3, H4, P, Blockquote, UL, Lead, Large, Small, Muted };