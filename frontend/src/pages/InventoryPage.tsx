import { useState, useEffect } from 'react'
import {
  Alert,
  Badge,
  Button,
  Card,
  Col,
  Form,
  Modal,
  ProgressBar,
  Row,
  Spinner
} from 'react-bootstrap'
import type { Inventory, InventoryRequest } from '../types'
import { getAllInventory, addInventory, updateStock } from '../api/inventoryApi'

const InventoryPage = () => {
  const [inventory, setInventory] = useState<Inventory[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [showAddModal, setShowAddModal] = useState(false)
  const [showStockModal, setShowStockModal] = useState(false)
  const [selectedProductId, setSelectedProductId] = useState(0)
  const [stockQty, setStockQty] = useState(0)

  const [form, setForm] = useState<InventoryRequest>({
    productId: 0,
    productName: '',
    availableStock: 0
  })

  const fetchInventory = async () => {
    setLoading(true)
    setError('')

    try {
      const res = await getAllInventory()
      setInventory(res.data.data || [])
    } catch {
      setError('Failed to fetch inventory. Check inventory-service and API Gateway.')
      setInventory([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchInventory()
  }, [])

  const handleAdd = async () => {
    if (form.productId <= 0 || !form.productName.trim() || form.availableStock < 0) {
      setError('Please enter valid product id, product name and stock.')
      return
    }

    try {
      await addInventory(form)
      setSuccess('Inventory added successfully')
      setError('')
      setShowAddModal(false)
      setForm({ productId: 0, productName: '', availableStock: 0 })
      fetchInventory()
    } catch {
      setError('Failed to add inventory')
    }
  }

  const handleUpdateStock = async () => {
    try {
      await updateStock(selectedProductId, stockQty)
      setSuccess('Stock updated successfully')
      setError('')
      setShowStockModal(false)
      setStockQty(0)
      fetchInventory()
    } catch {
      setError('Failed to update stock')
    }
  }

  const totalAvailable = inventory.reduce(
    (sum, item) => sum + Number(item.availableStock || 0),
    0
  )

  const totalReserved = inventory.reduce(
    (sum, item) => sum + Number(item.reservedStock || 0),
    0
  )

  const lowStockCount = inventory.filter(item => item.availableStock <= 3).length

  const getStockHealth = (availableStock: number) => {
    if (availableStock <= 0) return { label: 'Out of Stock', color: 'danger', progress: 5 }
    if (availableStock <= 3) return { label: 'Low Stock', color: 'warning', progress: 35 }
    if (availableStock <= 10) return { label: 'Healthy', color: 'info', progress: 70 }
    return { label: 'High Stock', color: 'success', progress: 100 }
  }

  return (
    <div className="page-container inventory-warehouse-page">
      <div className="warehouse-hero">
        <div>
          <Badge bg="light" text="dark">Warehouse Live View</Badge>
          <h1>Mercedes Stock Warehouse</h1>
          <p>
            View available cars, reserved stock and update inventory quantity in real time.
          </p>
        </div>

        <Button variant="light" onClick={() => setShowAddModal(true)}>
          + Add Inventory
        </Button>
      </div>

      <div className="warehouse-stats-strip">
        <div>
          <span>Total Models</span>
          <strong>{inventory.length}</strong>
        </div>

        <div>
          <span>Available Cars</span>
          <strong>{totalAvailable}</strong>
        </div>

        <div>
          <span>Reserved Cars</span>
          <strong>{totalReserved}</strong>
        </div>

        <div>
          <span>Low Stock</span>
          <strong>{lowStockCount}</strong>
        </div>
      </div>

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
      ) : inventory.length === 0 ? (
        <Alert variant="secondary">No inventory found</Alert>
      ) : (
        <Row className="g-4">
          {inventory.map(item => {
            const health = getStockHealth(item.availableStock)

            return (
              <Col xl={4} md={6} key={item.id}>
                <Card className="stock-card h-100">
                  <Card.Body>
                    <div className="stock-card-top">
                      <div className="stock-icon">▦</div>

                      <Badge bg={health.color}>
                        {health.label}
                      </Badge>
                    </div>

                    <h3>{item.productName}</h3>
                    <p>Product ID: {item.productId}</p>

                    <div className="stock-numbers">
                      <div>
                        <span>Available</span>
                        <strong>{item.availableStock}</strong>
                      </div>

                      <div>
                        <span>Reserved</span>
                        <strong>{item.reservedStock}</strong>
                      </div>
                    </div>

                    <div className="stock-health-bar">
                      <div className="d-flex justify-content-between mb-2">
                        <small>Stock Health</small>
                        <small>{health.progress}%</small>
                      </div>

                      <ProgressBar now={health.progress} variant={health.color} />
                    </div>

                    <Button
                      variant="outline-light"
                      className="w-100 mt-4"
                      onClick={() => {
                        setSelectedProductId(item.productId)
                        setShowStockModal(true)
                      }}
                    >
                      Update Stock
                    </Button>
                  </Card.Body>
                </Card>
              </Col>
            )
          })}
        </Row>
      )}

      <Modal show={showAddModal} onHide={() => setShowAddModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Add Inventory</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Product ID</Form.Label>
              <Form.Control
                type="number"
                value={form.productId}
                onChange={e => setForm({ ...form, productId: Number(e.target.value) })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Product Name</Form.Label>
              <Form.Control
                value={form.productName}
                onChange={e => setForm({ ...form, productName: e.target.value })}
                placeholder="Mercedes C-Class"
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Available Stock</Form.Label>
              <Form.Control
                type="number"
                value={form.availableStock}
                onChange={e => setForm({ ...form, availableStock: Number(e.target.value) })}
              />
            </Form.Group>
          </Form>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowAddModal(false)}>
            Cancel
          </Button>

          <Button variant="dark" onClick={handleAdd}>
            Add Inventory
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showStockModal} onHide={() => setShowStockModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Update Stock — Product #{selectedProductId}</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form.Group>
            <Form.Label>Quantity</Form.Label>
            <Form.Control
              type="number"
              value={stockQty}
              onChange={e => setStockQty(Number(e.target.value))}
              placeholder="Positive to add, negative to reduce"
            />

            <Form.Text>
              Example: 5 will add stock, -3 will reduce stock.
            </Form.Text>
          </Form.Group>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowStockModal(false)}>
            Cancel
          </Button>

          <Button variant="dark" onClick={handleUpdateStock}>
            Update Stock
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  )
}

export default InventoryPage