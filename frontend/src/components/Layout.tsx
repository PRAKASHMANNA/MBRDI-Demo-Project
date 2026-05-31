import type { ReactNode } from 'react'
import AppNavbar from './Navbar'

interface LayoutProps {
  children: ReactNode
}

const Layout = ({ children }: LayoutProps) => {
  return (
    <div className="app-shell">
      <AppNavbar />
      <main>{children}</main>
    </div>
  )
}

export default Layout