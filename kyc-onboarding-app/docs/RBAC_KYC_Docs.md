
# RBAC Policies – KYC Document Access

| Action | Requester | Reviewer | Admin |
|------|-----------|----------|-------|
| Upload documents | Own cases | ❌ | ✅ |
| View documents | ❌ | Assigned cases | ✅ |
| Download documents | ❌ | Assigned cases | ✅ |
| Delete documents | ❌ | ❌ | ✅ |

Enforced via:
- @PreAuthorize at controller
- Ownership/assignment checks in service layer
