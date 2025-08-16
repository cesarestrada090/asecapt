import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComplaintService, Complaint } from '../../services/complaint.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-complaints-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaints-management.component.html',
  styleUrl: './complaints-management.component.css'
})
export class ComplaintsManagementComponent implements OnInit {
  complaints: Complaint[] = [];
  filteredComplaints: Complaint[] = [];
  selectedComplaint: Complaint | null = null;

  // Loading states
  isLoading: boolean = false;
  isUpdating: boolean = false;

  // Response form
  responseText: string = '';
  showResponseModal: boolean = false;

  // Filters
  statusFilter: string = '';
  typeFilter: string = '';
  searchTerm: string = '';

  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 10;

  // Messages
  message: {type: 'success' | 'error', text: string} | null = null;

  constructor(private complaintService: ComplaintService) {}

  ngOnInit(): void {
    this.loadComplaints();
  }

  loadComplaints() {
    this.isLoading = true;
    this.complaintService.getAllComplaints()
      .pipe(
        catchError(error => {
          console.error('Error loading complaints:', error);
          this.showMessage('error', 'Error cargando reclamos');
          return of([]);
        })
      )
      .subscribe(complaints => {
        this.complaints = complaints.sort((a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        this.applyFilters();
        this.isLoading = false;
      });
  }

  applyFilters() {
    this.filteredComplaints = this.complaints.filter(complaint => {
      const matchesStatus = !this.statusFilter || complaint.status === this.statusFilter;
      const matchesType = !this.typeFilter || complaint.type === this.typeFilter;
      const matchesSearch = !this.searchTerm ||
        complaint.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        complaint.complaintNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        complaint.email.toLowerCase().includes(this.searchTerm.toLowerCase());

      return matchesStatus && matchesType && matchesSearch;
    });
  }

  onFilterChange() {
    this.applyFilters();
    this.currentPage = 1;
  }

  updateStatus(complaint: Complaint, newStatus: string) {
    this.isUpdating = true;
    this.complaintService.updateComplaintStatus(complaint.id, newStatus)
      .pipe(
        catchError(error => {
          console.error('Error updating status:', error);
          this.showMessage('error', 'Error actualizando estado');
          return of(null);
        })
      )
      .subscribe(updatedComplaint => {
        if (updatedComplaint) {
          const index = this.complaints.findIndex(c => c.id === complaint.id);
          if (index !== -1) {
            this.complaints[index] = updatedComplaint;
            this.applyFilters();
          }
          this.showMessage('success', 'Estado actualizado correctamente');
        }
        this.isUpdating = false;
      });
  }

  openResponseModal(complaint: Complaint) {
    this.selectedComplaint = complaint;
    this.responseText = complaint.response || '';
    this.showResponseModal = true;
  }

  closeResponseModal() {
    this.showResponseModal = false;
    this.selectedComplaint = null;
    this.responseText = '';
  }

  submitResponse() {
    if (!this.selectedComplaint || !this.responseText.trim()) {
      return;
    }

    this.isUpdating = true;
    this.complaintService.addResponse(this.selectedComplaint.id, this.responseText.trim())
      .pipe(
        catchError(error => {
          console.error('Error adding response:', error);
          this.showMessage('error', 'Error agregando respuesta');
          return of(null);
        })
      )
      .subscribe(updatedComplaint => {
        if (updatedComplaint) {
          const index = this.complaints.findIndex(c => c.id === this.selectedComplaint!.id);
          if (index !== -1) {
            this.complaints[index] = updatedComplaint;
            this.applyFilters();
          }
          this.showMessage('success', 'Respuesta agregada correctamente');
          this.closeResponseModal();
        }
        this.isUpdating = false;
      });
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'pendiente': return 'badge bg-warning text-dark';
      case 'en_proceso': return 'badge bg-info text-white';
      case 'resuelto': return 'badge bg-success text-white';
      default: return 'badge bg-secondary text-white';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'pendiente': return 'Pendiente';
      case 'en_proceso': return 'En Proceso';
      case 'resuelto': return 'Resuelto';
      default: return status;
    }
  }

  getTypeText(type: string): string {
    switch (type) {
      case 'reclamo': return 'Reclamo';
      case 'queja': return 'Queja';
      case 'sugerencia': return 'Sugerencia';
      default: return type;
    }
  }

  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return 'Fecha inválida';
    }
  }

  get paginatedComplaints(): Complaint[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredComplaints.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredComplaints.length / this.itemsPerPage);
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  showMessage(type: 'success' | 'error', text: string) {
    this.message = { type, text };
    setTimeout(() => {
      this.message = null;
    }, 5000);
  }

  clearFilters() {
    this.statusFilter = '';
    this.typeFilter = '';
    this.searchTerm = '';
    this.applyFilters();
    this.currentPage = 1;
  }

  trackByComplaintId(index: number, complaint: Complaint): string {
    return complaint.id;
  }

  // Statistics methods
  getPendingCount(): number {
    return this.complaints.filter(c => c.status === 'pendiente').length;
  }

  getInProgressCount(): number {
    return this.complaints.filter(c => c.status === 'en_proceso').length;
  }

  getResolvedCount(): number {
    return this.complaints.filter(c => c.status === 'resuelto').length;
  }

  getTotalCount(): number {
    return this.complaints.length;
  }
}
