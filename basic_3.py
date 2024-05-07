import sys
import time

def generate_string(base, indices):
    s = base
    # print(f"Initial string: {s}")  # Debug output to show the string before manipulation starts
    for idx in indices:
        # print(f"Current index: {idx}, Current length: {len(s)}")  # Debug each step
        if idx > len(s):  # If index is greater than current string length
            # print(f"Index {idx} out of bounds. Adjusting to {len(s)}")
            idx = len(s)  # Adjust index to the maximum valid value (end of string)
        s = s[:idx + 1] + s + s[idx+1:]  # Insert the whole string s at position idx + 1
        # print(f"String after index {idx} insertion: {s}")  # Show string after each insertion
    return s  # Return the modified string

def validate_base_sequence(base, valid_chars={'A', 'C', 'G', 'T'}):
    if any(char not in valid_chars for char in base):
        raise ValueError(f"Base sequence contains invalid characters: {base}")  # Validate characters in the DNA sequence

def read_input(file_path):
    generatedStrings = []
    indices = []
    base = ''
    lines = []
    with open(file_path, 'r') as file:  # Open file for reading
        lines = file.readlines()
    i = 0
    for j in range(2):
        base = lines[i].strip()
        while ((i+1) < len(lines)) and (lines[i+1].strip().isdigit()):
            i = i+1
            indices.append(int(lines[i]))
        generatedStrings.append(generate_string(base, indices))
        indices.clear()
        i = i+1

    x = (generatedStrings.__getitem__(0))
    y = (generatedStrings.__getitem__(1))

    return x,y

def sequence_alignment(x, y, gap_penalty, mismatch_cost):
    m, n = len(x), len(y)  # Get lengths of both sequences
    dp = [[0] * (n + 1) for _ in range(m + 1)]  # Initialize DP table with zeroes
    for i in range(1, m + 1):
        dp[i][0] = dp[i-1][0] + gap_penalty  # Initialize first column of DP table
    for j in range(1, n + 1):
        dp[0][j] = dp[0][j-1] + gap_penalty  # Initialize first row of DP table

    for i in range(1, m + 1):
        for j in range(1, n + 1):
            cost = mismatch_cost[x[i-1]][y[j-1]]  # Compute mismatch cost
            dp[i][j] = min(dp[i-1][j-1] + cost, dp[i][j-1] + gap_penalty, dp[i-1][j] + gap_penalty)  # Fill DP table

    # print_dp_matrix(dp)  # Debug: print DP matrix

    alignX, alignY = '', ''
    while m > 0 and n > 0:  # Trace back from bottom-right to top-left
        if dp[m][n] == dp[m-1][n-1] + mismatch_cost[x[m-1]][y[n-1]]:
            alignX = x[m-1] + alignX
            alignY = y[n-1] + alignY
            m -= 1
            n -= 1
        elif dp[m][n] == dp[m-1][n] + gap_penalty:
            alignX = x[m-1] + alignX
            alignY = '_' + alignY
            m -= 1
        else:
            alignX = '_' + alignX
            alignY = y[n-1] + alignY
            n -= 1

    while m > 0:  # Complete alignment for remaining characters in x
        alignX = x[m-1] + alignX
        alignY = '_' + alignY
        m -= 1
    while n > 0:  # Complete alignment for remaining characters in y
        alignX = '_' + alignX
        alignY = y[n-1] + alignY
        n -= 1
    
    return dp[len(x)][len(y)], alignX, alignY  # Return final alignment and score

# def print_dp_matrix(dp):
#     for row in dp:
#         print(' '.join(f"{val:3}" for val in row))  # Print each row of DP matrix formatted to align columns

def main(input_file, output_file):
    try:
        x, y = read_input(input_file)  # Read input and generate strings
        # print("Generated X:", x)
        # print("Generated Y:", y)
        gap_penalty = 30  # Set gap penalty
        mismatch_cost = {  # Define mismatch cost dictionary
            'A': {'A': 0, 'C': 110, 'G': 48, 'T': 94},
            'C': {'A': 110, 'C': 0, 'G': 118, 'T': 48},
            'G': {'A': 48, 'C': 118, 'G': 0, 'T': 110},
            'T': {'A': 94, 'C': 48, 'G': 110, 'T': 0}
        }

        start_time = time.time()  # Start timing
        cost, alignX, alignY = sequence_alignment(x, y, gap_penalty, mismatch_cost)  # Perform sequence alignment
        end_time = time.time()  # End timing

        elapsed_time = (end_time - start_time) * 1000  # Calculate elapsed time in milliseconds
        with open(output_file, 'w') as file:  # Open output file for writing
            file.write(f"{cost}\n{alignX}\n{alignY}\n{elapsed_time:.3f}\n")  # Write results to output file

    except ValueError as e:
        print(f"Error: {e}")  # Handle errors, e.g., invalid characters in DNA sequences

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script.py <input_file> <output_file>")  # Check command line arguments
    else:
        main(sys.argv[1], sys.argv[2])  # Execute main function with command line arguments
