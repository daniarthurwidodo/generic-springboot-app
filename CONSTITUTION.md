# Project Constitution

## Naming Conventions

### Test Methods
- Use **camelCase** for test method names (e.g., `helloShouldReturnHelloWorld`, `shouldReturnHelloWorldMessage`)
- Do **not** use snake_case (e.g., avoid `hello_should_return_hello_world`)
- Preferred patterns:
  - `<method>Should<expectedBehavior>` (e.g., `helloShouldReturnHelloWorld`)
  - `should<expectedBehavior>` (e.g., `shouldReturnHelloWorldMessage`)
