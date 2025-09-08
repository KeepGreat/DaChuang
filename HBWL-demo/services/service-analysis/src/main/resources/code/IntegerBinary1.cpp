#include <bits/stdc++.h>
using namespace std;

int a[100];
bool check(int x, int *a, int m, int n)
{
    int div = 1, sum = 0;
    for (int i = 0; i < n; i++)
    {
        sum += a[i];
        if (sum > x) {div++;    sum = a[i];}
    }
    return div <= m;
}

int main(void)
{
    int n, m;   cin >> n >> m;
    int maxn = 0, sum = 0;
    for (int i = 0; i < n; i++) {scanf("%d", &a[i]); sum+= a[i]; maxn = max(maxn, a[i]);}
    int left = maxn, right = sum;
    int ans = maxn;
    while (left < right)
    {
        int mid = (right + left) >> 1;
        if (check(mid, a, m, n))
        {
            ans = mid;  right = mid;
        }
        else    left = mid + 1;
    }
    printf("%d\n", ans);

    system("pause");
    return 0;
}