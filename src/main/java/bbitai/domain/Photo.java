package bbitai.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PHOTOS")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", nullable = false, length = 40)
	private String name;

	@Column(name = "upload_date", nullable = false)
	private LocalDateTime uploadDate;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "mime_type")
	private String mimeType;

	@Column(name = "file_size")
	private Long fileSize;

	@Column(name = "image_data")
	@Lob
	private byte[] imageData;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User uploadedBy;

	@PrePersist
	protected void onCreate() {
		if (uploadDate == null) {
			uploadDate = LocalDateTime.now();
		}
	}
}



