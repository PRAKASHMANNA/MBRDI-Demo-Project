import { useEffect, useMemo, useState } from 'react'
import {
  Alert,
  Badge,
  Button,
  Card,
  Col,
  Form,
  InputGroup,
  Modal,
  Row,
  Spinner
} from 'react-bootstrap'
import api from '../api/axios'
import type { Product, ProductRequest } from '../types'

const demoCars: Product[] = [
  {
    id: 1,
    name: 'Mercedes C-Class',
    description: 'Luxury sedan with premium comfort and performance.',
    price: 9000000,
    category: 'Sedan',
    stock: 10,
    createdAt: ''
  },
  {
    id: 2,
    name: 'Mercedes GLC',
    description: 'Premium SUV designed for power, comfort and style.',
    price: 11500000,
    category: 'SUV',
    stock: 6,
    createdAt: ''
  },
  {
    id: 3,
    name: 'Mercedes AMG GT',
    description: 'High-performance AMG sports car experience.',
    price: 22000000,
    category: 'AMG',
    stock: 3,
    createdAt: ''
  }
]

const ProductsPage = () => {
  const [products, setProducts] = useState<Product[]>(demoCars)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [searchTerm, setSearchTerm] = useState('')
  const [categoryFilter, setCategoryFilter] = useState('All')

  const [showAddModal, setShowAddModal] = useState(false)
  const [showBuyModal, setShowBuyModal] = useState(false)
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null)

  const [buyForm, setBuyForm] = useState({
    userId: 1,
    quantity: 1
  })

  const [form, setForm] = useState<ProductRequest>({
    name: '',
    description: '',
    price: 0,
    category: '',
    stock: 0
  })

  const fetchProducts = async () => {
    setLoading(true)
    setError('')

    try {
      const res = await api.get('/api/v1/products')
      setProducts(res.data.data || [])
    } catch (err) {
      console.error('Products fetch failed:', err)
      setError('Backend not reachable. Showing demo cars.')
      setProducts(demoCars)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchProducts()
  }, [])

  const categories = useMemo(() => {
    const values = products.map(product => product.category).filter(Boolean)
    return ['All', ...Array.from(new Set(values))]
  }, [products])

  const filteredProducts = useMemo(() => {
    return products.filter(product => {
      const category = product.category || ''
      const name = product.name || ''

      const matchesCategory =
        categoryFilter === 'All' || category === categoryFilter

      const matchesSearch =
        !searchTerm.trim() ||
        name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        category.toLowerCase().includes(searchTerm.toLowerCase())

      return matchesCategory && matchesSearch
    })
  }, [products, searchTerm, categoryFilter])

  const featuredCar = products[0] || demoCars[0]

  const totalStock = products.reduce(
    (sum, product) => sum + Number(product.stock || 0),
    0
  )

  const startingPrice = products.reduce((min, product) => {
    const price = Number(product.price || 0)

    if (price <= 0) return min
    if (min === 0) return price

    return Math.min(min, price)
  }, 0)

  const handleCreate = async () => {
    if (!form.name.trim() || !form.category.trim() || form.price <= 0) {
      setError('Please enter car name, category and valid price.')
      return
    }

    try {
      await api.post('/api/v1/products', form)

      setSuccess('Mercedes car added successfully')
      setError('')
      setShowAddModal(false)

      setForm({
        name: '',
        description: '',
        price: 0,
        category: '',
        stock: 0
      })

      fetchProducts()
    } catch (err) {
      console.error('Product create failed:', err)
      setError('Failed to create product. Check product-service.')
    }
  }

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this car?')) return

    try {
      await api.delete(`/api/v1/products/${id}`)

      setSuccess('Car deleted successfully')
      setError('')
      fetchProducts()
    } catch (err) {
      console.error('Product delete failed:', err)
      setError('Delete failed. Check product-service.')
    }
  }

  const openBuyModal = (product: Product) => {
    setSelectedProduct(product)
    setBuyForm({
      userId: 1,
      quantity: 1
    })
    setShowBuyModal(true)
  }

  const handleBuyNow = async () => {
    if (!selectedProduct) return

    if (buyForm.userId <= 0 || buyForm.quantity <= 0) {
      setError('Please enter valid user id and quantity.')
      return
    }

    try {
      const res = await api.post('/api/v1/orders', {
        userId: buyForm.userId,
        productId: selectedProduct.id,
        quantity: buyForm.quantity
      })

      const orderId = res.data.data?.id || '-'

      setSuccess(`Order #${orderId} placed successfully for ${selectedProduct.name}`)
      setError('')
      setShowBuyModal(false)
      fetchProducts()
    } catch (err) {
      console.error('Order create failed from product page:', err)
      setError('Order failed. Check order-service, product-service, Kafka and stock.')
    }
  }

  return (
    <div className="page-container benz-showroom-page">
      <section className="benz-hero">
        <div className="benz-hero-content">
          <Badge bg="light" text="dark">
            Mercedes-Benz Digital Showroom
          </Badge>

          <h1>Luxury cars, connected to your microservices.</h1>

          <p>
            Browse Mercedes-Benz models, check stock, place orders and trigger
            Kafka notifications through your full-stack ecommerce platform.
          </p>

          <div className="benz-hero-actions">
            <Button variant="light" size="lg" onClick={fetchProducts}>
              View Collection
            </Button>

            <Button
              variant="outline-light"
              size="lg"
              onClick={() => setShowAddModal(true)}
            >
              + Add Model
            </Button>
          </div>
        </div>

        <div className="benz-hero-car-panel">
          <div className="benz-car-glow" />
          <div className="benz-car-shape">
            <div className="benz-wheel left" />
            <div className="benz-wheel right" />
          </div>

          <div className="benz-feature-box">
            <span>Featured Model</span>
            <h3>{featuredCar.name}</h3>
            <p>{featuredCar.category}</p>
            <strong>
              ₹{Number(featuredCar.price || 0).toLocaleString('en-IN')}
            </strong>
          </div>
        </div>
      </section>

      <section className="benz-stats-row">
        <div>
          <span>Total Models</span>
          <strong>{products.length}</strong>
        </div>

        <div>
          <span>Total Stock</span>
          <strong>{totalStock}</strong>
        </div>

        <div>
          <span>Starting From</span>
          <strong>₹{startingPrice.toLocaleString('en-IN')}</strong>
        </div>

        <div>
          <span>Backend</span>
          <strong>8080</strong>
        </div>
      </section>

      <section className="benz-filter-panel">
        <div>
          <h2>Explore Models</h2>
          <p>Search and filter Mercedes-Benz cars by category.</p>
        </div>

        <div className="benz-filter-controls">
          <InputGroup>
            <Form.Control
              placeholder="Search C-Class, GLC, AMG..."
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
            />

            <Button
              variant="dark"
              onClick={() => {
                setSearchTerm('')
                setCategoryFilter('All')
                fetchProducts()
              }}
            >
              Reset
            </Button>
          </InputGroup>

          <div className="benz-category-tabs">
            {categories.map(category => (
              <button
                key={category}
                className={
                  categoryFilter === category
                    ? 'benz-category-tab active'
                    : 'benz-category-tab'
                }
                onClick={() => setCategoryFilter(category)}
              >
                {category}
              </button>
            ))}
          </div>
        </div>
      </section>

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess('')}>
          {success}
        </Alert>
      )}

      {loading ? (
        <div className="text-center mt-5">
          <Spinner animation="border" variant="light" />
        </div>
      ) : filteredProducts.length === 0 ? (
        <Alert variant="secondary">No cars found</Alert>
      ) : (
        <Row className="g-4">
          {filteredProducts.map(product => (
            <Col xl={4} lg={6} key={product.id}>
              <Card className="benz-model-card">
                <div className="benz-model-visual">
                  <div className="benz-model-light" />
                  <div className="benz-model-car">
                    <span className="wheel one" />
                    <span className="wheel two" />
                  </div>

                  <Badge
                    bg={product.stock > 0 ? 'success' : 'danger'}
                    className="benz-stock-badge"
                  >
                    {product.stock > 0 ? 'In Stock' : 'Out of Stock'}
                  </Badge>
                </div>

                <Card.Body>
                  <div className="benz-model-title-row">
                    <div>
                      <Badge bg="light" text="dark">
                        {product.category}
                      </Badge>

                      <h3>{product.name}</h3>
                    </div>

                    <div className="benz-model-id">#{product.id}</div>
                  </div>

                  <p className="benz-model-desc">
                    {product.description || 'Premium Mercedes-Benz model.'}
                  </p>

                  <div className="benz-model-info">
                    <div>
                      <span>Price</span>
                      <strong>
                        ₹{Number(product.price || 0).toLocaleString('en-IN')}
                      </strong>
                    </div>

                    <div>
                      <span>Stock</span>
                      <strong>{product.stock}</strong>
                    </div>
                  </div>

                  <div className="benz-card-actions">
                    <Button
                      variant="light"
                      disabled={product.stock <= 0}
                      onClick={() => openBuyModal(product)}
                    >
                      Buy Now
                    </Button>

                    <Button
                      variant="outline-danger"
                      onClick={() => handleDelete(product.id)}
                    >
                      Delete
                    </Button>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}

      <Modal show={showAddModal} onHide={() => setShowAddModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Add Mercedes Car</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Car Name</Form.Label>
              <Form.Control
                value={form.name}
                onChange={e => setForm({ ...form, name: e.target.value })}
                placeholder="Mercedes C-Class"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={form.description}
                onChange={e => setForm({ ...form, description: e.target.value })}
                placeholder="Luxury sedan with premium comfort"
              />
            </Form.Group>

            <Row>
              <Col>
                <Form.Group className="mb-3">
                  <Form.Label>Price ₹</Form.Label>
                  <Form.Control
                    type="number"
                    value={form.price}
                    onChange={e =>
                      setForm({ ...form, price: Number(e.target.value) })
                    }
                  />
                </Form.Group>
              </Col>

              <Col>
                <Form.Group className="mb-3">
                  <Form.Label>Stock</Form.Label>
                  <Form.Control
                    type="number"
                    value={form.stock}
                    onChange={e =>
                      setForm({ ...form, stock: Number(e.target.value) })
                    }
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group>
              <Form.Label>Category</Form.Label>
              <Form.Select
                value={form.category}
                onChange={e => setForm({ ...form, category: e.target.value })}
              >
                <option value="">Select category</option>
                <option>Sedan</option>
                <option>SUV</option>
                <option>Coupe</option>
                <option>Convertible</option>
                <option>AMG</option>
                <option>Electric</option>
              </Form.Select>
            </Form.Group>
          </Form>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowAddModal(false)}>
            Cancel
          </Button>

          <Button variant="dark" onClick={handleCreate}>
            Create Car
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showBuyModal} onHide={() => setShowBuyModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Confirm Mercedes Order</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {selectedProduct && (
            <div className="buy-summary">
              <h4>{selectedProduct.name}</h4>

              <p>
                {selectedProduct.description || 'Premium Mercedes-Benz model.'}
              </p>

              <div className="buy-price">
                ₹{Number(selectedProduct.price || 0).toLocaleString('en-IN')}
              </div>

              <Form.Group className="mb-3 mt-3">
                <Form.Label>User ID</Form.Label>
                <Form.Control
                  type="number"
                  min={1}
                  value={buyForm.userId}
                  onChange={e =>
                    setBuyForm({ ...buyForm, userId: Number(e.target.value) })
                  }
                />
              </Form.Group>

              <Form.Group>
                <Form.Label>Quantity</Form.Label>
                <Form.Control
                  type="number"
                  min={1}
                  max={selectedProduct.stock}
                  value={buyForm.quantity}
                  onChange={e =>
                    setBuyForm({ ...buyForm, quantity: Number(e.target.value) })
                  }
                />
              </Form.Group>
            </div>
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowBuyModal(false)}>
            Cancel
          </Button>

          <Button variant="dark" onClick={handleBuyNow}>
            Place Order
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  )
}

export default ProductsPage