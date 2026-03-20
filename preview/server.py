#!/usr/bin/env python3
import http.server
import socketserver
import os

PORT = 8080
os.chdir(os.path.dirname(os.path.abspath(__file__)))

class Handler(http.server.SimpleHTTPRequestHandler):
    def log_message(self, format, *args):
        print(f"[DriveFlow] {self.address_string()} - {format % args}")

print(f"DriveFlow Preview Server running at http://localhost:{PORT}")
print("Pages: / | /search.html | /booking.html | /payment.html | /confirmation.html | /admin.html")

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    httpd.serve_forever()
