import request from '@/utils/request';

export let executeCode = (CodeSandboxInput) => {
    return request.post('/api/codesandbox/execute', CodeSandboxInput)
}