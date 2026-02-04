import request from '@/api/request';

export let executeCode = (CodeSandboxInput) => {
    return request.post('/api/codesandbox/execute', CodeSandboxInput)
}