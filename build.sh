#!/bin/bash
# Build script for Wordle Swing application

echo "=== Building Wordle (Swing Version) ==="

# Create build directory
mkdir -p build

# Compile all source files
echo "Compiling Java files..."
javac -d build \
    src/Main.java \
    src/UI.java \
    ressource/CheckAlgo.java \
    ressource/Debug.java \
    ressource/GameField.java \
    ressource/Variables.java \
    ressource/Wortliste.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    
    # Copy resources
    echo "Copying resources..."
    cp -r ressource/*.txt build/ressource/ 2>/dev/null
    cp -r ressource/*.png build/ressource/ 2>/dev/null
    
    echo "✓ Build complete!"
    echo ""
    echo "To run the application:"
    echo "  cd build && java -cp .:.. src.Main"
else
    echo "✗ Compilation failed!"
    exit 1
fi
