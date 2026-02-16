# Quick Reference: Photo Album Implementation

## Files Modified

### Core Implementation Files
1. **PhotoServiceController.java** - Implements PhotosApi and AuthApi interfaces
2. **PhotoService.java** - Service interface with CRUD and auth methods
3. **PhotoServiceImpl.java** - Complete implementation of PhotoService
4. **Photo.java** - Entity with imageData field for binary storage
5. **PsConfiguration.java** - Added PasswordEncoder and ModelMapper beans
6. **pom.xml** - Updated Java version to 17

### Database
- **db_init.sql** - Added image_data VARBINARY(MAX) column

## Key Implementation Details

### Photo CRUD Operations

**List Photos:**
```
GET /photos?sortBy=date&order=desc
```
- Default sort: by date, descending
- Supports: sortBy (name/date), order (asc/desc)

**Upload Photo:**
```
POST /photos
Authentication: Required (Bearer token)
Content-Type: multipart/form-data
Parameters:
  - name: string (max 40 chars)
  - file: binary
```
- Returns: 201 Created with Photo details
- Returns: 401 Unauthorized if not authenticated
- Returns: 400 Bad Request if name > 40 chars

**Get Photo Details:**
```
GET /photos/{photoId}
```
- Returns: Photo object with metadata
- Returns: 404 Not Found if photo doesn't exist

**Get Photo Image:**
```
GET /photos/{photoId}/image
```
- Returns: Binary image data
- Content-Type: Based on stored MIME type
- Returns: 404 Not Found if photo doesn't exist

**Delete Photo:**
```
DELETE /photos/{photoId}
Authentication: Required (Bearer token)
```
- Returns: 200 OK on success
- Returns: 401 Unauthorized if not authenticated
- Returns: 403 Forbidden if user doesn't own photo
- Returns: 404 Not Found if photo doesn't exist

### User Management

**Register:**
```
POST /auth/register
Content-Type: application/json
{
  "username": "user123",
  "password": "password123"
}
```
- Returns: 201 Created with UserResponse
- Returns: 409 Conflict if user already exists
- Returns: 400 Bad Request if missing fields

**Login:**
```
POST /auth/login
Content-Type: application/json
{
  "username": "user123",
  "password": "password123"
}
```
- Returns: 200 OK with LoginResponse (includes JWT token)
- Returns: 401 Unauthorized if invalid credentials
- Token in response: { "token": "jwt_token", "expiresIn": 3600, "user": {...} }

**Logout:**
```
POST /auth/logout
Authentication: Required (Bearer token)
```
- Returns: 200 OK with success message
- Returns: 401 Unauthorized if not authenticated

## Database Schema

### USERS Table
```sql
- id: BIGINT PRIMARY KEY
- username: NVARCHAR(255) UNIQUE
- password: NVARCHAR(MAX) (BCrypt hashed)
- created_at: DATETIME2
```

### PHOTOS Table
```sql
- id: BIGINT PRIMARY KEY
- name: NVARCHAR(40)
- upload_date: DATETIME2 (auto-set on insert)
- image_url: NVARCHAR(MAX) (path reference)
- mime_type: NVARCHAR(50) (e.g., "image/jpeg")
- file_size: BIGINT (in bytes)
- image_data: VARBINARY(MAX) (binary content)
- user_id: BIGINT (FK to USERS)
```

## Type Conversions

The API uses UUID for IDs, but the database uses BIGINT (Long):
- Database: `Long photoId` (e.g., 1, 2, 3...)
- API: `UUID photoId` (e.g., generated from Long ID)
- Conversion: `UUID.nameUUIDFromBytes(longId.toString().getBytes())`

## Error Handling

All endpoints return appropriate HTTP status codes:

| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (validation failed) |
| 401 | Unauthorized (auth required/failed) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 409 | Conflict (duplicate resource) |
| 500 | Internal Server Error |

## Performance Considerations

1. **Caching**: Photo details are cached with key=photoId
2. **Database Sorting**: Uses Spring Data Sort API (not in-memory)
3. **Binary Storage**: VARBINARY(MAX) suitable for photos up to 2GB
4. **Lazy Loading**: User entity loaded lazily from Photo

## Security Notes

1. **Passwords**: Hashed with BCrypt (10 rounds)
2. **Ownership**: Photo delete validates user ownership
3. **Auth Required**: Upload and delete endpoints require authentication
4. **Token**: Currently uses placeholder JWT (TODO: implement real JWT)

## Future Enhancements

1. ✅ Implement real JWT token generation and validation
2. ✅ Implement user ID lookup from authenticated context
3. ✅ Add max file size validation (413 Response)
4. ✅ Add image format validation
5. ✅ Implement pagination for photo listing
6. ✅ Add search/filter functionality
7. ✅ Consider external storage for high-volume scenarios

## Testing the API

### Example cURL commands:

```bash
# Register user
curl -X POST http://localhost:3000/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'

# Login
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'

# Upload photo
curl -X POST http://localhost:3000/photos \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "name=My Photo" \
  -F "file=@/path/to/photo.jpg"

# List photos
curl http://localhost:3000/photos?sortBy=date&order=desc

# Get photo
curl http://localhost:3000/photos/{photoId}

# Get image
curl http://localhost:3000/photos/{photoId}/image > photo.jpg

# Delete photo
curl -X DELETE http://localhost:3000/photos/{photoId} \
  -H "Authorization: Bearer YOUR_TOKEN"

# Logout
curl -X POST http://localhost:3000/auth/logout \
  -H "Authorization: Bearer YOUR_TOKEN"
```

