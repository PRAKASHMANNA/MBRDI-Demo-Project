import { Link, useLocation } from 'react-router-dom'
import { Navbar, Nav, Container } from 'react-bootstrap'

const AppNavbar = () => {
  const location = useLocation()

  const isActive = (path: string) => location.pathname === path

  return (
    <Navbar expand="lg" sticky="top" className="luxury-navbar">
      <Container fluid className="px-4 px-lg-5">
        <Navbar.Brand as={Link} to="/products" className="brand-logo">
          <span className="brand-icon">✦</span>
          <span>Mercedes-Benz Store</span>
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="navbar-nav" />

        <Navbar.Collapse id="navbar-nav">
          <Nav className="ms-auto nav-pill-group">
            <Nav.Link
              as={Link}
              to="/products"
              active={isActive('/products')}
            >
              Cars
            </Nav.Link>

            <Nav.Link
              as={Link}
              to="/orders"
              active={isActive('/orders')}
            >
              Orders
            </Nav.Link>

            <Nav.Link
              as={Link}
              to="/inventory"
              active={isActive('/inventory')}
            >
              Inventory
            </Nav.Link>

            <Nav.Link
              as={Link}
              to="/notifications"
              active={isActive('/notifications')}
            >
              Notifications
            </Nav.Link>

            <Nav.Link
              as={Link}
              to="/users"
              active={isActive('/users')}
            >
              Users
            </Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}

export default AppNavbar