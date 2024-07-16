describe('sts-client-example App', () => {
  it('should display welcome message', () => {
    cy.visit('/')
    cy.get('app-root h1').should('have.text', 'Welcome to app!')
  })
})
