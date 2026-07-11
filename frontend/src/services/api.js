const API_BASE_URL = 'http://localhost:8080/api/csv';

export async function previewCsvFile(file) {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`${API_BASE_URL}/preview`, {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    throw new Error('Failed to fetch CSV preview from the server');
  }

  return response.json();
}

export async function importCsvFile(file) {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`${API_BASE_URL}/import`, {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    throw new Error('Failed to import and process CSV file via AI');
  }

  return response.json();
}
