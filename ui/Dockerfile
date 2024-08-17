# Use an existing image as a base
FROM node:latest

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy package.json and yarn.lock files to the working directory
COPY package.json yarn.lock ./

# Install Yarn dependencies
RUN yarn install

# Copy all project files to the working directory
COPY . .

# Build the React app
RUN yarn build

# Expose the port your app runs on
EXPOSE 3000

# Command to run your app (change this according to your project)
CMD ["yarn", "dev"]