# Document Store Project
This project implements a Document Store, which efficiently stores and retrieves documents along with their contents. The system utilizes several key data structures and techniques to handle document storage, retrieval, and memory management.

## Features
- **B-Tree**: Used for storing and retrieving documents. When memory limits are reached, documents are offloaded to disk for efficient storage management.
- **Min-Heap**: Tracks documents by their last access time, enabling intelligent selection of documents to send to disk under memory constraints.
- **Trie Table**: Allows fast retrieval of documents based on specific words or phrases for efficient search capabilities.
- **Undo Functionality**: Implemented using Stacks to support undo operations for document additions or deletions, utilizing lambda functions.
- **JSON Serialization/Deserialization**: Documents are serialized to JSON when offloaded to disk, and deserialized when retrieved, ensuring seamless data persistence.

## Data Structures Used
- **B-Tree**: Efficient document management and storage.
- **Min-Heap**: Manages document memory usage by last access time.
- **Trie**: Supports fast document lookup based on content.
- **Stacks**: Facilitates undo functionality for document operations.

## Usage
This project is designed to handle document storage with a memory limit, leveraging data structures to optimize retrieval, storage, and memory usage. Documents are automatically moved to disk when memory limits are exceeded, and users can undo document add/delete actions.

### Serialization and Persistence
All documents sent to disk are serialized to JSON format for storage. When retrieved from disk, documents are deserialized back into the system for continued use.

